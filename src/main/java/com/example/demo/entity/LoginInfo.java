package com.example.demo.entity;

public class LoginInfo {
    private String ip;
    private int port;
    private String userName;
    private String pwd;

    public LoginInfo(String ip, int port, String userName, String pwd) {
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.pwd = pwd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
