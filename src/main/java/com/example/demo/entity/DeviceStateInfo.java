package com.example.demo.entity;

import com.example.demo.lib.NetSDKLib;

public class DeviceStateInfo {
    private String ipAndUserName;
    private NetSDKLib.NET_DEVICEINFO_Ex m_stDeviceInfo;  // 设备信息
    private NetSDKLib.LLong m_hLoginHandle = new NetSDKLib.LLong(0); // 登陆句柄;m_hLoginHandle.longValue() != 0 Login Success
    private NetSDKLib.LLong m_hAttachHandle = new NetSDKLib.LLong(0);  //订阅;m_hAttachHandle.longValue() == 0停止订阅
    private float faceDetect = 0;
    private String faceBase64Data;

    public String getIpAndUserName() {
        return ipAndUserName;
    }

    public void setIpAndUserName(String ipAndUserName) {
        this.ipAndUserName = ipAndUserName;
    }

    public NetSDKLib.NET_DEVICEINFO_Ex getM_stDeviceInfo() {
        return m_stDeviceInfo;
    }

    public void setM_stDeviceInfo(NetSDKLib.NET_DEVICEINFO_Ex m_stDeviceInfo) {
        this.m_stDeviceInfo = m_stDeviceInfo;
    }

    public NetSDKLib.LLong getM_hLoginHandle() {
        return m_hLoginHandle;
    }

    public void setM_hLoginHandle(NetSDKLib.LLong m_hLoginHandle) {
        this.m_hLoginHandle = m_hLoginHandle;
    }

    public NetSDKLib.LLong getM_hAttachHandle() {
        return m_hAttachHandle;
    }

    public void setM_hAttachHandle(NetSDKLib.LLong m_hAttachHandle) {
        this.m_hAttachHandle = m_hAttachHandle;
    }

    public float getFaceDetect() {
        return faceDetect;
    }

    public void setFaceDetect(float faceDetect) {
        this.faceDetect = faceDetect;
    }

    public String getFaceBase64Data() {
        return faceBase64Data;
    }

    public void setFaceBase64Data(String faceBase64Data) {
        this.faceBase64Data = faceBase64Data;
    }
}
