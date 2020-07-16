package com.sanyi.faceRecognition.entity;

public class FaceDetect {
    private float similarityDegree;
    private String faceData;

    public float getSimilarityDegree() {
        return similarityDegree;
    }

    public void setSimilarityDegree(float similarityDegree) {
        this.similarityDegree = similarityDegree;
    }

    public String getFaceData() {
        return faceData;
    }

    public void setFaceData(String faceData) {
        this.faceData = faceData;
    }
}
