package com.example.demo.service;


import com.example.demo.entity.DeviceStateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.lib.NetSDKLib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

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
            Data.userLoginMap.put(ipAndUserName,m_hLoginHandle);
            Data.deviceStateInfoMap.put(m_hLoginHandle,deviceStateInfo);
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
            Data.userLoginMap.remove(deviceStateInfo.getIpAndUserName());
            Data.deviceStateInfoMap.remove(m_hLoginHandle);
            logger.info("Logout Success.");
        }

        return bRet;
    }
}
