package org.classapp.signlanguage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.PacketGetter;
import com.google.protobuf.InvalidProtocolBufferException;

import org.classapp.signlanguage.ml.TslLstmModel;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PredictionActivity extends TranslatorActivity {

    private LandmarkProto.LandmarkList poseLandmarksTmp;
    private LandmarkProto.LandmarkList rightHandLandmarksTmp;
    private LandmarkProto.LandmarkList leftHandLandmarksTmp;
    private LandmarkProto.LandmarkList faceLandmarksTmp;

    private List<Float> concatFeatureList = new ArrayList<>();

    private List<String> translateList = new ArrayList<>();

    private final String[] classArr = {"ขอบคุณ", "ทำงาน", "ธุระ", "รัก", "สบายดี", "สวัสดี", "หิว", "เข้าใจ", "เสียใจ", "ไม่สบาย"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                            List<Float> angleList = Preprocessing.extractAngles(this.poseLandmarksTmp, this.leftHandLandmarksTmp, this.rightHandLandmarksTmp);

                            // Extract forehand and backhand
                            List<Float> forehandBackhandList = Preprocessing.extractForehandBackhand(this.leftHandLandmarksTmp, this.rightHandLandmarksTmp);

                            // Extract hand position
                            List<Float> handPositionList = Preprocessing.extractHandPosition(this.poseLandmarksTmp, this.faceLandmarksTmp, this.leftHandLandmarksTmp, this.rightHandLandmarksTmp);

                            // Add all feature
                            this.concatFeatureList.addAll(angleList);
                            this.concatFeatureList.addAll(forehandBackhandList);
                            this.concatFeatureList.addAll(handPositionList);

                            if (this.concatFeatureList.size() == Preprocessing.ARR_LENGTH) {
                                if (this.translateList.size() >= 3) {
                                    this.translateList.clear();
                                }

                                // Convert Float[] -> float[]
                                float[] frameArr = new float[this.concatFeatureList.size()];
                                for (int index = 0; index < frameArr.length; index++) {
                                    frameArr[index] = this.concatFeatureList.get(index).floatValue();
                                }

                                String translate = prediction(frameArr);

                                this.translateList.add(translate);
                                textTranslation.setText(String.join("   ", this.translateList));

                                this.concatFeatureList.clear();
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

    private String prediction(float[] data) {
        String result = "";
        try {
            TslLstmModel model = TslLstmModel.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 16, 68}, DataType.FLOAT32);
            inputFeature0.loadArray(data);

            TslLstmModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0.0f;
            for (int index = 0; index < confidences.length; index++) {
                if (confidences[index] > maxConfidence) {
                    maxConfidence = confidences[index];
                    maxPos = index;
                }
            }

            result = this.classArr[maxPos];

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void onBackPressed() {

    }
}
