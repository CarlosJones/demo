package com.sanyi.faceRecognition.service;


import com.sanyi.faceRecognition.entity.DetectPersonInfo;
import com.sanyi.faceRecognition.entity.DeviceStateInfo;
import com.sanyi.faceRecognition.entity.FaceDetect;
import com.sanyi.faceRecognition.entity.LoginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sanyi.faceRecognition.lib.NetSDKLib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private Data data;
    @Autowired
    private CallBackService callBackService;
    @Autowired
    private LoadPictureService loadPictureService;

    public NetSDKLib.LLong login(String m_strIp, int m_nPort, String m_strUser, String m_strPassword) {
        // 设备信息
        NetSDKLib.NET_DEVICEINFO_Ex m_stDeviceInfo = new NetSDKLib.NET_DEVICEINFO_Ex();
        // 登陆句柄
        NetSDKLib.LLong m_hLoginHandle = new NetSDKLib.LLong(0);

        //IntByReference nError = new IntByReference(0);
        //入参
        NetSDKLib.NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY pstInParam=new NetSDKLib.NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY();
        pstInParam.nPort=m_nPort;
        pstInParam.szIP=m_strIp.getBytes();
        pstInParam.szPassword=m_strPassword.getBytes();
        pstInParam.szUserName=m_strUser.getBytes();
        //出参
        NetSDKLib.NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY pstOutParam=new NetSDKLib.NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY();
        pstOutParam.stuDeviceInfo=m_stDeviceInfo;

        //m_hLoginHandle = netsdk.CLIENT_LoginEx2(m_strIp, m_nPort, m_strUser, m_strPassword, 0, null, m_stDeviceInfo, nError);
        m_hLoginHandle=Data.netsdk.CLIENT_LoginWithHighLevelSecurity(pstInParam, pstOutParam);

        if(m_hLoginHandle.longValue() == 0) {
            logger.error("Login Device[ "+m_strIp+" ] Port[ "+m_nPort+" ]Failed.");
        } else {
            DeviceStateInfo deviceStateInfo = new DeviceStateInfo();
            String ipAndUserName = m_strIp+":"+m_strUser;
            deviceStateInfo.setIpAndUserName(ipAndUserName);
            deviceStateInfo.setM_hLoginHandle(m_hLoginHandle);
            deviceStateInfo.setM_stDeviceInfo(m_stDeviceInfo);
            data.userLoginMap.put(ipAndUserName,m_hLoginHandle);
            data.deviceStateInfoMap.put(m_hLoginHandle,deviceStateInfo);
            System.out.println("loginHandle:"+m_hLoginHandle);
            logger.info("Login Success [ " + m_strIp + " ]");
        }

        return m_hLoginHandle;
    }

    public boolean logout(DeviceStateInfo deviceStateInfo) {
        NetSDKLib.LLong m_hLoginHandle = deviceStateInfo.getM_hLoginHandle();

        if(m_hLoginHandle.longValue() == 0) {
            return false;
        }

        boolean bRet = Data.netsdk.CLIENT_Logout(m_hLoginHandle);


        if(bRet) {
//            m_hLoginHandle.setValue(0);
            data.userLoginMap.remove(deviceStateInfo.getIpAndUserName());
            data.deviceStateInfoMap.remove(m_hLoginHandle);
            logger.info("Logout Success.");
        }

        return bRet;
    }

    public void loginFunction(String doctorName, String idCard, String userName){
        Map<String, LoginInfo> loginMap = new HashMap<>();

        DetectPersonInfo detectPersonInfo = new DetectPersonInfo(userName,idCard);
        FaceDetect faceDetect = new FaceDetect();
        boolean result = false;
        NetSDKLib.LLong loginResult = null;      //login handler
        DeviceStateInfo deviceStateInfo = null;
        LoginInfo loginInfo = loginMap.get(doctorName);
        if(null != loginInfo){
            String ipAndUsername = loginInfo.getIp() + ":" + loginInfo.getUserName();
            NetSDKLib.LLong loginHandle = data.userLoginMap.get(ipAndUsername);
            if(null != loginHandle){
                deviceStateInfo = data.deviceStateInfoMap.get(loginHandle);
                if(null == deviceStateInfo ){
                    loginResult = login(loginInfo.getIp(),loginInfo.getPort(),loginInfo.getUserName(),loginInfo.getPwd());
                    logger.info("[ " +ipAndUsername+ " ] Login result is : " + loginResult);
                }else{
                    NetSDKLib.LLong m_hLoginHandle = deviceStateInfo.getM_hLoginHandle();
                    if(m_hLoginHandle.longValue() == 0){
                        loginResult = login(loginInfo.getIp(),loginInfo.getPort(),loginInfo.getUserName(),loginInfo.getPwd());
                        System.out.println("[ " +ipAndUsername+ " ] Login result is : " + loginResult);
                        logger.info("[ " +ipAndUsername+ " ] Login result is : " + loginResult.longValue());
                    }else{
                        loginResult = deviceStateInfo.getM_hLoginHandle();
                        System.out.println("[ " +ipAndUsername+ " ] Login result is : " + loginResult);
                    }
                }
            }else{
                loginResult = login(loginInfo.getIp(),loginInfo.getPort(),loginInfo.getUserName(),loginInfo.getPwd());
                System.out.println("[ " +ipAndUsername+ " ] Login result is : " + loginResult);
                logger.info("[ " +ipAndUsername+ " ] Login result is : " + loginResult);
            }

            //maybe refreshed
            deviceStateInfo = data.deviceStateInfoMap.get(loginResult);
            if(null != deviceStateInfo){
                NetSDKLib.LLong m_hLoginHandle = deviceStateInfo.getM_hLoginHandle();
                System.out.println("m_hLoginHandle:"+m_hLoginHandle);
                NetSDKLib.LLong m_hAttachHandle = deviceStateInfo.getM_hAttachHandle();
                if(m_hAttachHandle.longValue() != 0){
                    data.loadLoginMap.remove(m_hAttachHandle);
                    deviceStateInfo.setFaceBase64Data(null);
                    deviceStateInfo.getM_hAttachHandle().setValue(0);
                }

                NetSDKLib.LLong attachHandle = loadPictureService.realLoadPicture(m_hLoginHandle,0, callBackService);
                result = (attachHandle.longValue() != 0);
                System.out.println("[ " +ipAndUsername+ " ] attach result is : " + result);
                logger.info("[ " +ipAndUsername+ " ] attach result is : " + result);
                if(attachHandle.longValue() != 0){
                    deviceStateInfo.setM_hAttachHandle(attachHandle);
                    data.loadLoginMap.put(attachHandle,loginResult);
                }
            }

        }
        System.gc();
    }


}
