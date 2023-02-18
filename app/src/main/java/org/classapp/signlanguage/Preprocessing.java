package org.classapp.signlanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import com.chaquo.python.PyObject;
import com.google.mediapipe.formats.proto.LandmarkProto;

public class Preprocessing {

    public static Integer SEQUENCE_LENGTH = 16;

    private static List<List<Integer>> jointHandList = new ArrayList<>();
    private static List<List<Integer>> jointPoseLeftList = new ArrayList<>();
    private static List<List<Integer>> jointPoseRightList = new ArrayList<>();

    public static void setUpPreprocessing() {
        setUpJointList();
    }

    private static void setUpJointList() {
        // นิ้วโป้ง
        jointHandList.add(Arrays.asList(new Integer[] {4,3,2}));
        jointHandList.add(Arrays.asList(new Integer[] {3,2,1}));
        jointHandList.add(Arrays.asList(new Integer[] {2,1,0}));

        // นิ้วชี้
        jointHandList.add(Arrays.asList(new Integer[] {8,7,6}));
        jointHandList.add(Arrays.asList(new Integer[] {7,6,5}));
        jointHandList.add(Arrays.asList(new Integer[] {6,5,0}));
        jointHandList.add(Arrays.asList(new Integer[] {6,5,9}));

        // นิ้วกลาง
        jointHandList.add(Arrays.asList(new Integer[] {12,11,10}));
        jointHandList.add(Arrays.asList(new Integer[] {11,10,9}));
        jointHandList.add(Arrays.asList(new Integer[] {10,9,0}));
        jointHandList.add(Arrays.asList(new Integer[] {10,9,5}));
        jointHandList.add(Arrays.asList(new Integer[] {10,9,13}));

        // นิ้วนาง
        jointHandList.add(Arrays.asList(new Integer[] {16,15,14}));
        jointHandList.add(Arrays.asList(new Integer[] {15,14,13}));
        jointHandList.add(Arrays.asList(new Integer[] {14,13,0}));
        jointHandList.add(Arrays.asList(new Integer[] {14,13,9}));
        jointHandList.add(Arrays.asList(new Integer[] {14,13,17}));

        // นิ้วก้อย
        jointHandList.add(Arrays.asList(new Integer[] {20,19,18}));
        jointHandList.add(Arrays.asList(new Integer[] {19,18,17}));
        jointHandList.add(Arrays.asList(new Integer[] {18,17,0}));
        jointHandList.add(Arrays.asList(new Integer[] {18,17,13}));

        // ข้อมือ
        jointHandList.add(Arrays.asList(new Integer[] {1,0,5}));
        jointHandList.add(Arrays.asList(new Integer[] {5,0,17}));

        // แขนซ้าย
        jointPoseLeftList.add(Arrays.asList(new Integer[] {15,13,11}));
        jointPoseLeftList.add(Arrays.asList(new Integer[] {19,15,13}));

        // แขนขวา
        jointPoseRightList.add(Arrays.asList(new Integer[] {16,14,12}));
        jointPoseRightList.add(Arrays.asList(new Integer[] {20,16,14}));
    }

    private static Double calculateAngle(List<Double> landmarkA, List<Double> landmarkB, List<Double> landmarkC) {
        Double radians = Math.atan2(landmarkC.get(1) - landmarkB.get(1), landmarkC.get(0) - landmarkB.get(0)) - Math.atan2(landmarkA.get(1) - landmarkB.get(1), landmarkA.get(0) - landmarkB.get(0));
        Double angle = Math.abs( (radians * 180.0) / Math.PI );

        if (angle > 180.0) {
            angle = 360 - angle;
        }

        return angle;
    }

    public static List<Double> extractAngles(LandmarkProto.LandmarkList poseLandmarks,
                                             LandmarkProto.LandmarkList leftHandLandmarks,
                                             LandmarkProto.LandmarkList rightHandLandmarks) {
        List<Double> angleList = new ArrayList<>();

        List<Double> angleLeftList = new ArrayList<>();
        List<Double> angleRightList = new ArrayList<>();

        List<Double> anglePoseLeftList = new ArrayList<>();
        List<Double> anglePoseRightList = new ArrayList<>();

        if (leftHandLandmarks != null) {

            // คำนวณ angle ของมือซ้าย
            for (List<Integer> joint : jointHandList) {
                List<Double> landmarkA = new ArrayList<>();
                landmarkA.add((double)leftHandLandmarks.getLandmark(joint.get(0)).getX());
                landmarkA.add((double)leftHandLandmarks.getLandmark(joint.get(0)).getY());
                landmarkA.add((double)leftHandLandmarks.getLandmark(joint.get(0)).getZ());

                List<Double> landmarkB = new ArrayList<>();
                landmarkB.add((double)leftHandLandmarks.getLandmark(joint.get(1)).getX());
                landmarkB.add((double)leftHandLandmarks.getLandmark(joint.get(1)).getY());
                landmarkB.add((double)leftHandLandmarks.getLandmark(joint.get(1)).getZ());

                List<Double> landmarkC = new ArrayList<>();
                landmarkC.add((double)leftHandLandmarks.getLandmark(joint.get(2)).getX());
                landmarkC.add((double)leftHandLandmarks.getLandmark(joint.get(2)).getY());
                landmarkC.add((double)leftHandLandmarks.getLandmark(joint.get(2)).getZ());

                angleLeftList.add(calculateAngle(landmarkA, landmarkB, landmarkC));
//                angleLeftList.add(preprocessingModule.callAttr("calculate_angles", landmarkA.toArray(), landmarkB.toArray(), landmarkC.toArray()).toDouble());
            }

            // คำนวณ angle ของแขนซ้าย
            for (List<Integer> joint : jointPoseLeftList) {
                List<Double> landmarkA = new ArrayList<>();
                landmarkA.add((double)poseLandmarks.getLandmark(joint.get(0)).getX());
                landmarkA.add((double)poseLandmarks.getLandmark(joint.get(0)).getY());
                landmarkA.add((double)poseLandmarks.getLandmark(joint.get(0)).getZ());

                List<Double> landmarkB = new ArrayList<>();
                landmarkB.add((double)poseLandmarks.getLandmark(joint.get(1)).getX());
                landmarkB.add((double)poseLandmarks.getLandmark(joint.get(1)).getY());
                landmarkB.add((double)poseLandmarks.getLandmark(joint.get(1)).getZ());

                List<Double> landmarkC = new ArrayList<>();
                landmarkC.add((double)poseLandmarks.getLandmark(joint.get(2)).getX());
                landmarkC.add((double)poseLandmarks.getLandmark(joint.get(2)).getY());
                landmarkC.add((double)poseLandmarks.getLandmark(joint.get(2)).getZ());

                anglePoseLeftList.add(calculateAngle(landmarkA, landmarkB, landmarkC));
//                anglePoseLeftList.add(preprocessingModule.callAttr("calculate_angles", landmarkA.toArray(), landmarkB.toArray(), landmarkC.toArray()).toDouble());
            }

        }else {
            Double[] zeroAnglesLeft = new Double[jointHandList.size()];
            Arrays.fill(zeroAnglesLeft, 0.0);
            angleLeftList = Arrays.asList(zeroAnglesLeft);

            Double[] zeroAnglesPoseLeft = new Double[jointPoseLeftList.size()];
            Arrays.fill(zeroAnglesPoseLeft, 0.0);
            anglePoseLeftList = Arrays.asList(zeroAnglesPoseLeft);
        }

        if (rightHandLandmarks != null) {

            // คำนวณ angle ของมือขวา
            for (List<Integer> joint : jointHandList) {
                List<Double> landmarkA = new ArrayList<>();
                landmarkA.add((double)rightHandLandmarks.getLandmark(joint.get(0)).getX());
                landmarkA.add((double)rightHandLandmarks.getLandmark(joint.get(0)).getY());
                landmarkA.add((double)rightHandLandmarks.getLandmark(joint.get(0)).getZ());

                List<Double> landmarkB = new ArrayList<>();
                landmarkB.add((double)rightHandLandmarks.getLandmark(joint.get(1)).getX());
                landmarkB.add((double)rightHandLandmarks.getLandmark(joint.get(1)).getY());
                landmarkB.add((double)rightHandLandmarks.getLandmark(joint.get(1)).getZ());

                List<Double> landmarkC = new ArrayList<>();
                landmarkC.add((double)rightHandLandmarks.getLandmark(joint.get(2)).getX());
                landmarkC.add((double)rightHandLandmarks.getLandmark(joint.get(2)).getY());
                landmarkC.add((double)rightHandLandmarks.getLandmark(joint.get(2)).getZ());

                angleRightList.add(calculateAngle(landmarkA, landmarkB, landmarkC));
//                angleRightList.add(preprocessingModule.callAttr("calculate_angles", landmarkA.toArray(), landmarkB.toArray(), landmarkC.toArray()).toDouble());
//                Double[] zeroAnglesRight = new Double[jointHandList.size()];
//                Arrays.fill(zeroAnglesRight, 0.0);
//                angleRightList = Arrays.asList(zeroAnglesRight);
            }

            // คำนวณ angle ของแขนขวา
            for (List<Integer> joint : jointPoseRightList) {
                List<Double> landmarkA = new ArrayList<>();
                landmarkA.add((double)poseLandmarks.getLandmark(joint.get(0)).getX());
                landmarkA.add((double)poseLandmarks.getLandmark(joint.get(0)).getY());
                landmarkA.add((double)poseLandmarks.getLandmark(joint.get(0)).getZ());

                List<Double> landmarkB = new ArrayList<>();
                landmarkB.add((double)poseLandmarks.getLandmark(joint.get(1)).getX());
                landmarkB.add((double)poseLandmarks.getLandmark(joint.get(1)).getY());
                landmarkB.add((double)poseLandmarks.getLandmark(joint.get(1)).getZ());

                List<Double> landmarkC = new ArrayList<>();
                landmarkC.add((double)poseLandmarks.getLandmark(joint.get(2)).getX());
                landmarkC.add((double)poseLandmarks.getLandmark(joint.get(2)).getY());
                landmarkC.add((double)poseLandmarks.getLandmark(joint.get(2)).getZ());

                anglePoseRightList.add(calculateAngle(landmarkA, landmarkB, landmarkC));
//                anglePoseRightList.add(preprocessingModule.callAttr("calculate_angles", landmarkA.toArray(), landmarkB.toArray(), landmarkC.toArray()).toDouble());
//                Double[] zeroAnglesPoseRight = new Double[jointPoseRightList.size()];
//                Arrays.fill(zeroAnglesPoseRight, 0.0);
//                anglePoseRightList = Arrays.asList(zeroAnglesPoseRight);
            }

        }else {
            Double[] zeroAnglesRight = new Double[jointHandList.size()];
            Arrays.fill(zeroAnglesRight, 0.0);
            angleRightList = Arrays.asList(zeroAnglesRight);

            Double[] zeroAnglesPoseRight = new Double[jointPoseRightList.size()];
            Arrays.fill(zeroAnglesPoseRight, 0.0);
            anglePoseRightList = Arrays.asList(zeroAnglesPoseRight);
        }

        angleList.addAll(angleLeftList);
        angleList.addAll(angleRightList);
        angleList.addAll(anglePoseLeftList);
        angleList.addAll(anglePoseRightList);

        return angleList;
    }

    public static List<Double> extractForehandBackhand(LandmarkProto.LandmarkList leftHandLandmarks,
                                                       LandmarkProto.LandmarkList rightHandLandmarks) {
        List<Double> forehandBackHandList = new ArrayList<>();

        Double noLeftHand = 1.0;
        Double forehandLeftHand = 0.0;
        Double backhandLeftHand = 0.0;

        Double noRightHand = 1.0;
        Double forehandRightHand = 0.0;
        Double backhandRightHand = 0.0;

        // กรณีมือซ้าย
        if (leftHandLandmarks != null) {
            noLeftHand = 0.0;

            LandmarkProto.Landmark leftThumb =  leftHandLandmarks.getLandmark(2);
            LandmarkProto.Landmark leftPinky = leftHandLandmarks.getLandmark(17);
            LandmarkProto.Landmark leftWrist = leftHandLandmarks.getLandmark(0);

            if (leftWrist.getY() > leftThumb.getY() && leftWrist.getY() > leftPinky.getY()) {
                if (leftThumb.getX() < leftPinky.getX()) {
                    forehandLeftHand = 1.0;
                    backhandLeftHand = 0.0;
                }else {
                    backhandLeftHand = 1.0;
                    forehandLeftHand = 0.0;
                }
            } else if (leftWrist.getY() < leftThumb.getY() && leftWrist.getY() < leftPinky.getY()) {
                if (leftThumb.getX() < leftPinky.getX()) {
                    backhandLeftHand = 1.0;
                    forehandLeftHand = 0.0;
                }else {
                    forehandLeftHand = 1.0;
                    backhandLeftHand = 0.0;
                }
            }else {
                if (leftWrist.getX() > leftThumb.getX() && leftWrist.getX() > leftPinky.getX()) {
                    if (leftThumb.getY() > leftPinky.getY()) {
                        forehandLeftHand = 1.0;
                        backhandLeftHand = 0.0;
                    }else {
                        backhandLeftHand = 1.0;
                        forehandLeftHand = 0.0;
                    }
                } else if (leftWrist.getX() < leftThumb.getX() && leftWrist.getX() < leftPinky.getX()) {
                    if (leftThumb.getY() < leftPinky.getY()) {
                        forehandLeftHand = 1.0;
                        backhandLeftHand = 0.0;
                    }else {
                        backhandLeftHand = 1.0;
                        forehandLeftHand = 0.0;
                    }
                }
            }
        }else {
            noLeftHand = 1.0;
            forehandLeftHand = 0.0;
            backhandLeftHand = 0.0;
        }

        // กรณีมือขวา
        if (rightHandLandmarks != null) {
            noRightHand = 0.0;

            LandmarkProto.Landmark rightThumb = rightHandLandmarks.getLandmark(2);
            LandmarkProto.Landmark rightPinky = rightHandLandmarks.getLandmark(17);
            LandmarkProto.Landmark rightWrist = rightHandLandmarks.getLandmark(0);

            if (rightWrist.getY() > rightThumb.getY() && rightWrist.getY() > rightPinky.getY()) {
                if (rightThumb.getX() > rightPinky.getX()) {
                    forehandRightHand = 1.0;
                    backhandRightHand = 0.0;
                }else {
                    backhandRightHand = 1.0;
                    forehandRightHand = 0.0;
                }
            } else if (rightWrist.getY() < rightThumb.getY() && rightWrist.getY() < rightPinky.getY()) {
                if (rightThumb.getX() > rightPinky.getX()) {
                    backhandRightHand = 1.0;
                    forehandRightHand = 0.0;
                }else {
                    forehandRightHand = 1.0;
                    backhandRightHand = 0.0;
                }
            }else {
                if (rightWrist.getX() < rightThumb.getX() && rightWrist.getX() < rightPinky.getX()) {
                    if (rightThumb.getY() > rightPinky.getY()) {
                        forehandRightHand = 1.0;
                        backhandRightHand = 0.0;
                    }else {
                        backhandRightHand = 1.0;
                        forehandRightHand = 0.0;
                    }
                } else if (rightWrist.getX() > rightThumb.getX() && rightWrist.getX() > rightPinky.getX()) {
                    if (rightThumb.getY() < rightPinky.getY()) {
                        forehandRightHand = 1.0;
                        backhandRightHand = 0.0;
                    }else {
                        backhandRightHand = 1.0;
                        forehandRightHand = 0.0;
                    }
                }
            }
        }else {
            noRightHand = 1.0;
            forehandRightHand = 0.0;
            backhandRightHand = 0.0;
        }

        forehandBackHandList.add(noLeftHand);
        forehandBackHandList.add(forehandLeftHand);
        forehandBackHandList.add(backhandLeftHand);

        forehandBackHandList.add(noRightHand);
        forehandBackHandList.add(forehandRightHand);
        forehandBackHandList.add(backhandRightHand);

        return forehandBackHandList;
    }

    public static List<Double> extractHandPosition(LandmarkProto.LandmarkList poseLandmarks,
                                                   LandmarkProto.LandmarkList faceLandmarks,
                                                   LandmarkProto.LandmarkList leftHandLandmarks,
                                                   LandmarkProto.LandmarkList rightHandLandmarks) {
        List<Double> handPositionList = new ArrayList<>();

        Double noLeftHandPose = 1.0;
        Double inPosePositionLeft = 0.0;
        Double outPosePositionLeft = 0.0;

        Double noLeftHandFace = 1.0;
        Double inFacePositionLeft = 0.0;
        Double outFacePositionLeft = 0.0;

        Double noRightHandPose = 1.0;
        Double inPosePositionRight = 0.0;
        Double outPosePositionRight = 0.0;

        Double noRightHandFace = 1.0;
        Double inFacePositionRight = 0.0;
        Double outFacePositionRight = 0.0;

        if (poseLandmarks != null && faceLandmarks != null) {
            LandmarkProto.Landmark leftShoulderPose = poseLandmarks.getLandmark(11);
            LandmarkProto.Landmark rightShoulderPose = poseLandmarks.getLandmark(12);

            float faceMinX = (float) faceLandmarks.getLandmarkList().stream().mapToDouble(v -> v.getX()).min().getAsDouble();
            float faceMaxX = (float) faceLandmarks.getLandmarkList().stream().mapToDouble(v -> v.getX()).max().getAsDouble();

            float faceMinY = (float) faceLandmarks.getLandmarkList().stream().mapToDouble(v -> v.getY()).min().getAsDouble();
            float faceMaxY = (float) faceLandmarks.getLandmarkList().stream().mapToDouble(v -> v.getY()).max().getAsDouble();

            // กรณีมือซ้าย
            if (leftHandLandmarks != null) {
                noLeftHandPose = 0.0;
                noLeftHandFace = 0.0;

                LandmarkProto.Landmark middleFinger = leftHandLandmarks.getLandmark(9);

                // กรณีลำตัว
                if (middleFinger.getX() > rightShoulderPose.getX() &&
                        middleFinger.getX() < leftShoulderPose.getX() &&
                        (middleFinger.getY() > rightShoulderPose.getY() ||
                                middleFinger.getY() > leftShoulderPose.getY())) {
                    inPosePositionLeft = 1.0;
                    outPosePositionLeft = 0.0;
                }else {
                    outPosePositionLeft = 1.0;
                    inPosePositionLeft = 0.0;
                }

                // กรณีใบหน้า
                if (middleFinger.getX() > faceMinX && middleFinger.getX() < faceMaxX &&
                        middleFinger.getY() > faceMinY && middleFinger.getY() < faceMaxY) {
                    inFacePositionLeft = 1.0;
                    outFacePositionLeft = 0.0;
                }else {
                    outFacePositionLeft = 1.0;
                    inFacePositionLeft = 0.0;
                }
            }else {
                noLeftHandPose = 1.0;
                inPosePositionLeft = 0.0;
                outPosePositionLeft = 0.0;

                noLeftHandFace = 1.0;
                inFacePositionLeft = 0.0;
                outFacePositionLeft = 0.0;
            }

            // กรณีมือขวา
            if (rightHandLandmarks != null) {
                noRightHandPose = 0.0;
                noRightHandFace = 0.0;

                LandmarkProto.Landmark middleFinger = rightHandLandmarks.getLandmark(9);

                // กรณีลำตัว
                if (middleFinger.getX() > rightShoulderPose.getX() &&
                        middleFinger.getX() < leftShoulderPose.getX() &&
                        (middleFinger.getY() > rightShoulderPose.getY() ||
                                middleFinger.getY() > leftShoulderPose.getY())) {
                    inPosePositionRight = 1.0;
                    outPosePositionRight = 0.0;
                }else {
                    outPosePositionRight = 1.0;
                    inPosePositionRight = 0.0;
                }

                // กรณีใบหน้า
                if (middleFinger.getX() > faceMinX && middleFinger.getX() < faceMaxX &&
                        middleFinger.getY() > faceMinY && middleFinger.getY() < faceMaxY) {
                    inFacePositionRight = 1.0;
                    outFacePositionRight = 0.0;
                }else {
                    outFacePositionRight = 1.0;
                    inFacePositionRight = 0.0;
                }
            }else {
                noRightHandPose = 1.0;
                inPosePositionRight = 0.0;
                outPosePositionRight = 0.0;

                noRightHandFace = 1.0;
                inFacePositionRight = 0.0;
                outFacePositionRight = 0.0;
            }
        }

        handPositionList.add(noLeftHandPose);
        handPositionList.add(inPosePositionLeft);
        handPositionList.add(outPosePositionLeft);

        handPositionList.add(noLeftHandFace);
        handPositionList.add(inFacePositionLeft);
        handPositionList.add(outFacePositionLeft);

        handPositionList.add(noRightHandPose);
        handPositionList.add(inPosePositionRight);
        handPositionList.add(outPosePositionRight);

        handPositionList.add(noRightHandFace);
        handPositionList.add(inFacePositionRight);
        handPositionList.add(outFacePositionRight);

        return handPositionList;
    }

}
