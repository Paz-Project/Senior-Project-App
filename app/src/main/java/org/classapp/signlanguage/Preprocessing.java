package org.classapp.signlanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.mediapipe.formats.proto.LandmarkProto;

public class Preprocessing {

    public static Integer SEQUENCE_LENGTH = 10;
    public static Integer FEATURE_LENGTH = 50;
    public static Integer ARR_LENGTH = SEQUENCE_LENGTH * FEATURE_LENGTH;

    private static List<List<Integer>> jointHandList = new ArrayList<>();
    private static List<List<Integer>> jointPoseLeftList = new ArrayList<>();
    private static List<List<Integer>> jointPoseRightList = new ArrayList<>();

    public static void setUpPreprocessing() {
        setUpJointList();
    }

    private static void setUpJointList() {
        jointHandList.clear();
        jointPoseLeftList.clear();
        jointPoseRightList.clear();

        // นิ้วโป้ง
        jointHandList.add(Arrays.asList(new Integer[] {4,3,2}));
        jointHandList.add(Arrays.asList(new Integer[] {3,2,1}));
//        jointHandList.add(Arrays.asList(new Integer[] {2,1,0}));

        // นิ้วชี้
        jointHandList.add(Arrays.asList(new Integer[] {8,7,6}));
        jointHandList.add(Arrays.asList(new Integer[] {7,6,5}));
        jointHandList.add(Arrays.asList(new Integer[] {6,5,0}));
//        jointHandList.add(Arrays.asList(new Integer[] {6,5,9}));

        // นิ้วกลาง
        jointHandList.add(Arrays.asList(new Integer[] {12,11,10}));
        jointHandList.add(Arrays.asList(new Integer[] {11,10,9}));
        jointHandList.add(Arrays.asList(new Integer[] {10,9,0}));
//        jointHandList.add(Arrays.asList(new Integer[] {10,9,5}));
//        jointHandList.add(Arrays.asList(new Integer[] {10,9,13}));

        // นิ้วนาง
        jointHandList.add(Arrays.asList(new Integer[] {16,15,14}));
        jointHandList.add(Arrays.asList(new Integer[] {15,14,13}));
        jointHandList.add(Arrays.asList(new Integer[] {14,13,0}));
//        jointHandList.add(Arrays.asList(new Integer[] {14,13,9}));
//        jointHandList.add(Arrays.asList(new Integer[] {14,13,17}));

        // นิ้วก้อย
        jointHandList.add(Arrays.asList(new Integer[] {20,19,18}));
        jointHandList.add(Arrays.asList(new Integer[] {19,18,17}));
        jointHandList.add(Arrays.asList(new Integer[] {18,17,0}));
//        jointHandList.add(Arrays.asList(new Integer[] {18,17,13}));

        // ข้อมือ
//        jointHandList.add(Arrays.asList(new Integer[] {1,0,5}));
//        jointHandList.add(Arrays.asList(new Integer[] {5,0,17}));

        // แขนซ้าย
        jointPoseLeftList.add(Arrays.asList(new Integer[] {15,13,11}));
        jointPoseLeftList.add(Arrays.asList(new Integer[] {19,15,13}));

        // แขนขวา
        jointPoseRightList.add(Arrays.asList(new Integer[] {16,14,12}));
        jointPoseRightList.add(Arrays.asList(new Integer[] {20,16,14}));
    }

    private static Float calculateAngle(List<Double> landmarkA, List<Double> landmarkB, List<Double> landmarkC) {
        Double radians = Math.atan2(landmarkC.get(1) - landmarkB.get(1), landmarkC.get(0) - landmarkB.get(0)) - Math.atan2(landmarkA.get(1) - landmarkB.get(1), landmarkA.get(0) - landmarkB.get(0));
        Double angle = Math.abs( (radians * 180.0) / Math.PI );

        if (angle > 180.0) {
            angle = 360 - angle;
        }

        return (float) (angle / 180.0);
    }

    public static List<Float> extractAngles(LandmarkProto.LandmarkList poseLandmarks,
                                            LandmarkProto.LandmarkList leftHandLandmarks,
                                            LandmarkProto.LandmarkList rightHandLandmarks) {
        List<Float> angleList = new ArrayList<>();

        List<Float> angleLeftList = new ArrayList<>();
        List<Float> angleRightList = new ArrayList<>();

        List<Float> anglePoseLeftList = new ArrayList<>();
        List<Float> anglePoseRightList = new ArrayList<>();

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
            }

        }else {
            Float[] zeroAnglesLeft = new Float[jointHandList.size()];
            Arrays.fill(zeroAnglesLeft, 0.0f);
            angleLeftList = Arrays.asList(zeroAnglesLeft);

            Float[] zeroAnglesPoseLeft = new Float[jointPoseLeftList.size()];
            Arrays.fill(zeroAnglesPoseLeft, 0.0f);
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
            }

        }else {
            Float[] zeroAnglesRight = new Float[jointHandList.size()];
            Arrays.fill(zeroAnglesRight, 0.0f);
            angleRightList = Arrays.asList(zeroAnglesRight);

            Float[] zeroAnglesPoseRight = new Float[jointPoseRightList.size()];
            Arrays.fill(zeroAnglesPoseRight, 0.0f);
            anglePoseRightList = Arrays.asList(zeroAnglesPoseRight);
        }

        angleList.addAll(angleLeftList);
        angleList.addAll(angleRightList);
        angleList.addAll(anglePoseLeftList);
        angleList.addAll(anglePoseRightList);

        return angleList;
    }

    public static List<Float> extractForehandBackhand(LandmarkProto.LandmarkList leftHandLandmarks,
                                                      LandmarkProto.LandmarkList rightHandLandmarks) {
        List<Float> forehandBackHandList = new ArrayList<>();

        Float noLeftHand = 1.0f;
        Float forehandLeftHand = 0.0f;
        Float backhandLeftHand = 0.0f;

        Float noRightHand = 1.0f;
        Float forehandRightHand = 0.0f;
        Float backhandRightHand = 0.0f;

        // กรณีมือซ้าย
        if (leftHandLandmarks != null) {
            noLeftHand = 0.0f;

            LandmarkProto.Landmark leftThumb =  leftHandLandmarks.getLandmark(2);
            LandmarkProto.Landmark leftPinky = leftHandLandmarks.getLandmark(17);
            LandmarkProto.Landmark leftWrist = leftHandLandmarks.getLandmark(0);

            if (leftWrist.getY() > leftThumb.getY() && leftWrist.getY() > leftPinky.getY()) {
                if (leftThumb.getX() < leftPinky.getX()) {
                    forehandLeftHand = 1.0f;
                    backhandLeftHand = 0.0f;
                }else {
                    backhandLeftHand = 1.0f;
                    forehandLeftHand = 0.0f;
                }
            } else if (leftWrist.getY() < leftThumb.getY() && leftWrist.getY() < leftPinky.getY()) {
                if (leftThumb.getX() < leftPinky.getX()) {
                    backhandLeftHand = 1.0f;
                    forehandLeftHand = 0.0f;
                }else {
                    forehandLeftHand = 1.0f;
                    backhandLeftHand = 0.0f;
                }
            }else {
                if (leftWrist.getX() > leftThumb.getX() && leftWrist.getX() > leftPinky.getX()) {
                    if (leftThumb.getY() > leftPinky.getY()) {
                        forehandLeftHand = 1.0f;
                        backhandLeftHand = 0.0f;
                    }else {
                        backhandLeftHand = 1.0f;
                        forehandLeftHand = 0.0f;
                    }
                } else if (leftWrist.getX() < leftThumb.getX() && leftWrist.getX() < leftPinky.getX()) {
                    if (leftThumb.getY() < leftPinky.getY()) {
                        forehandLeftHand = 1.0f;
                        backhandLeftHand = 0.0f;
                    }else {
                        backhandLeftHand = 1.0f;
                        forehandLeftHand = 0.0f;
                    }
                }
            }
        }else {
            noLeftHand = 1.0f;
            forehandLeftHand = 0.0f;
            backhandLeftHand = 0.0f;
        }

        // กรณีมือขวา
        if (rightHandLandmarks != null) {
            noRightHand = 0.0f;

            LandmarkProto.Landmark rightThumb = rightHandLandmarks.getLandmark(2);
            LandmarkProto.Landmark rightPinky = rightHandLandmarks.getLandmark(17);
            LandmarkProto.Landmark rightWrist = rightHandLandmarks.getLandmark(0);

            if (rightWrist.getY() > rightThumb.getY() && rightWrist.getY() > rightPinky.getY()) {
                if (rightThumb.getX() > rightPinky.getX()) {
                    forehandRightHand = 1.0f;
                    backhandRightHand = 0.0f;
                }else {
                    backhandRightHand = 1.0f;
                    forehandRightHand = 0.0f;
                }
            } else if (rightWrist.getY() < rightThumb.getY() && rightWrist.getY() < rightPinky.getY()) {
                if (rightThumb.getX() > rightPinky.getX()) {
                    backhandRightHand = 1.0f;
                    forehandRightHand = 0.0f;
                }else {
                    forehandRightHand = 1.0f;
                    backhandRightHand = 0.0f;
                }
            }else {
                if (rightWrist.getX() < rightThumb.getX() && rightWrist.getX() < rightPinky.getX()) {
                    if (rightThumb.getY() > rightPinky.getY()) {
                        forehandRightHand = 1.0f;
                        backhandRightHand = 0.0f;
                    }else {
                        backhandRightHand = 1.0f;
                        forehandRightHand = 0.0f;
                    }
                } else if (rightWrist.getX() > rightThumb.getX() && rightWrist.getX() > rightPinky.getX()) {
                    if (rightThumb.getY() < rightPinky.getY()) {
                        forehandRightHand = 1.0f;
                        backhandRightHand = 0.0f;
                    }else {
                        backhandRightHand = 1.0f;
                        forehandRightHand = 0.0f;
                    }
                }
            }
        }else {
            noRightHand = 1.0f;
            forehandRightHand = 0.0f;
            backhandRightHand = 0.0f;
        }

        forehandBackHandList.add(noLeftHand);
        forehandBackHandList.add(forehandLeftHand);
        forehandBackHandList.add(backhandLeftHand);

        forehandBackHandList.add(noRightHand);
        forehandBackHandList.add(forehandRightHand);
        forehandBackHandList.add(backhandRightHand);

        return forehandBackHandList;
    }

    public static List<Float> extractHandPosition(LandmarkProto.LandmarkList poseLandmarks,
                                                  LandmarkProto.LandmarkList faceLandmarks,
                                                  LandmarkProto.LandmarkList leftHandLandmarks,
                                                  LandmarkProto.LandmarkList rightHandLandmarks) {
        List<Float> handPositionList = new ArrayList<>();

        Float noLeftHandPose = 1.0f;
        Float inPosePositionLeft = 0.0f;
        Float outPosePositionLeft = 0.0f;

        Float noLeftHandFace = 1.0f;
        Float inFacePositionLeft = 0.0f;
        Float outFacePositionLeft = 0.0f;

        Float noRightHandPose = 1.0f;
        Float inPosePositionRight = 0.0f;
        Float outPosePositionRight = 0.0f;

        Float noRightHandFace = 1.0f;
        Float inFacePositionRight = 0.0f;
        Float outFacePositionRight = 0.0f;

        if (poseLandmarks != null && faceLandmarks != null) {
            LandmarkProto.Landmark leftShoulderPose = poseLandmarks.getLandmark(11);
            LandmarkProto.Landmark rightShoulderPose = poseLandmarks.getLandmark(12);

            float faceMinX = (float) faceLandmarks.getLandmarkList().stream().mapToDouble(v -> v.getX()).min().getAsDouble();
            float faceMaxX = (float) faceLandmarks.getLandmarkList().stream().mapToDouble(v -> v.getX()).max().getAsDouble();

            float faceMinY = (float) faceLandmarks.getLandmarkList().stream().mapToDouble(v -> v.getY()).min().getAsDouble();
            float faceMaxY = (float) faceLandmarks.getLandmarkList().stream().mapToDouble(v -> v.getY()).max().getAsDouble();

            // กรณีมือซ้าย
            if (leftHandLandmarks != null) {
                noLeftHandPose = 0.0f;
                noLeftHandFace = 0.0f;

                LandmarkProto.Landmark middleFinger = leftHandLandmarks.getLandmark(9);

                // กรณีลำตัว
                if (middleFinger.getX() > rightShoulderPose.getX() &&
                        middleFinger.getX() < leftShoulderPose.getX() &&
                        (middleFinger.getY() > rightShoulderPose.getY() ||
                                middleFinger.getY() > leftShoulderPose.getY())) {
                    inPosePositionLeft = 1.0f;
                    outPosePositionLeft = 0.0f;
                }else {
                    outPosePositionLeft = 1.0f;
                    inPosePositionLeft = 0.0f;
                }

                // กรณีใบหน้า
                if (middleFinger.getX() > faceMinX && middleFinger.getX() < faceMaxX &&
                        middleFinger.getY() > faceMinY && middleFinger.getY() < faceMaxY) {
                    inFacePositionLeft = 1.0f;
                    outFacePositionLeft = 0.0f;
                }else {
                    outFacePositionLeft = 1.0f;
                    inFacePositionLeft = 0.0f;
                }
            }else {
                noLeftHandPose = 1.0f;
                inPosePositionLeft = 0.0f;
                outPosePositionLeft = 0.0f;

                noLeftHandFace = 1.0f;
                inFacePositionLeft = 0.0f;
                outFacePositionLeft = 0.0f;
            }

            // กรณีมือขวา
            if (rightHandLandmarks != null) {
                noRightHandPose = 0.0f;
                noRightHandFace = 0.0f;

                LandmarkProto.Landmark middleFinger = rightHandLandmarks.getLandmark(9);

                // กรณีลำตัว
                if (middleFinger.getX() > rightShoulderPose.getX() &&
                        middleFinger.getX() < leftShoulderPose.getX() &&
                        (middleFinger.getY() > rightShoulderPose.getY() ||
                                middleFinger.getY() > leftShoulderPose.getY())) {
                    inPosePositionRight = 1.0f;
                    outPosePositionRight = 0.0f;
                }else {
                    outPosePositionRight = 1.0f;
                    inPosePositionRight = 0.0f;
                }

                // กรณีใบหน้า
                if (middleFinger.getX() > faceMinX && middleFinger.getX() < faceMaxX &&
                        middleFinger.getY() > faceMinY && middleFinger.getY() < faceMaxY) {
                    inFacePositionRight = 1.0f;
                    outFacePositionRight = 0.0f;
                }else {
                    outFacePositionRight = 1.0f;
                    inFacePositionRight = 0.0f;
                }
            }else {
                noRightHandPose = 1.0f;
                inPosePositionRight = 0.0f;
                outPosePositionRight = 0.0f;

                noRightHandFace = 1.0f;
                inFacePositionRight = 0.0f;
                outFacePositionRight = 0.0f;
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
