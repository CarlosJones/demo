package com.example.demo.entity;

public class DetectPersonInfo {
    private String userName;
    private String idCard;

    public DetectPersonInfo() {}

    public DetectPersonInfo(String userName, String idCard) {
        this.userName = userName;
        this.idCard = idCard;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}
