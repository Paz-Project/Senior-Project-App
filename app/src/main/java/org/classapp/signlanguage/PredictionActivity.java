package org.classapp.signlanguage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.PacketGetter;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

public class PredictionActivity extends TranslatorActivity {

    private LandmarkProto.LandmarkList poseLandmarksTmp;
    private LandmarkProto.LandmarkList rightHandLandmarksTmp;
    private LandmarkProto.LandmarkList leftHandLandmarksTmp;
    private LandmarkProto.LandmarkList faceLandmarksTmp;

    private PyObject predictionModule;

    private List<List<Double>> frameList = new ArrayList<>();

    private List<String> translateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Python py = Python.getInstance();

        this.predictionModule = py.getModule("prediction");

        TextView textTranslation = findViewById(R.id.textTranslation);

        Preprocessing.setUpPreprocessing();

        Button backToMenu = (Button) findViewById(R.id.backToMenu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PredictionActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });

        // Access right hand landmarks
        processor
                .addPacketCallback("right_hand_landmarks", (rightHandPacket -> {
                    byte[] rightHandLandmarksRaw = PacketGetter.getProtoBytes(rightHandPacket);
                    try {
                        this.rightHandLandmarksTmp = LandmarkProto.LandmarkList.parseFrom(rightHandLandmarksRaw);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }));

        // Access left hand landmarks
        processor
                .addPacketCallback("left_hand_landmarks", (leftHandPacket -> {
                    byte[] leftHandLandmarksRaw = PacketGetter.getProtoBytes(leftHandPacket);
                    try {
                        this.leftHandLandmarksTmp = LandmarkProto.LandmarkList.parseFrom(leftHandLandmarksRaw);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }));

        // Access face landmarks
        processor
                .addPacketCallback("face_landmarks", (facePacket -> {
                    byte[] faceLandmarksRaw = PacketGetter.getProtoBytes(facePacket);
                    try {
                        this.faceLandmarksTmp = LandmarkProto.LandmarkList.parseFrom(faceLandmarksRaw);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }));

        // Access pose landmarks
        processor
                .addPacketCallback("pose_landmarks", (posePacket -> {
                    byte[] poseLandmarksRaw = PacketGetter.getProtoBytes(posePacket);
                    try {
                        this.poseLandmarksTmp = LandmarkProto.LandmarkList.parseFrom(poseLandmarksRaw);

                        if (textTranslation.getText().toString().trim().equals("ไม่พบบุคคลภายในกล้อง")) {
                            textTranslation.setText("ไม่ทราบท่าทาง");
                        }

                        if (this.faceLandmarksTmp != null &&
                                (this.rightHandLandmarksTmp != null || this. leftHandLandmarksTmp != null)) {
                            // ** Data preprocessing **

                            // Extract angle
                            List<Double> angleList = Preprocessing.extractAngles(this.poseLandmarksTmp, this.leftHandLandmarksTmp, this.rightHandLandmarksTmp);

                            // Extract forehand and backhand
                            List<Double> forehandBackhandList = Preprocessing.extractForehandBackhand(this.leftHandLandmarksTmp, this.rightHandLandmarksTmp);

                            // Extract hand position
                            List<Double> handPositionList = Preprocessing.extractHandPosition(this.poseLandmarksTmp, this.faceLandmarksTmp, this.leftHandLandmarksTmp, this.rightHandLandmarksTmp);

                            // Add all feature
                            List<Double> featureList = new ArrayList<>();
                            featureList.addAll(angleList);
                            featureList.addAll(forehandBackhandList);
                            featureList.addAll(handPositionList);

                            this.frameList.add(featureList);

                            if (this.frameList.size() == Preprocessing.SEQUENCE_LENGTH) {
                                if (this.translateList.size() >= 3) {
                                    this.translateList.clear();
                                }

                                // แปลง list เป็น Double[][]
                                Double[][] frameArr = this.frameList.stream().map(features -> {
                                    return features.toArray(new Double[68]);
                                }).toArray(Double[][]::new);

                                String translate = this.predictionModule.callAttr("translation", frameArr, "").toString();
                                this.translateList.add(translate);
                                textTranslation.setText(String.join("   ", this.translateList));

                                this.frameList.clear();
                            }

                            this.poseLandmarksTmp = null;
                            this.faceLandmarksTmp = null;
                            this.rightHandLandmarksTmp = null;
                            this.leftHandLandmarksTmp = null;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }));

    }
    @Override
    public void onBackPressed() {

    }
}
