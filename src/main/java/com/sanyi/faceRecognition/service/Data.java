package com.sanyi.faceRecognition.service;

import com.sanyi.faceRecognition.entity.DeviceStateInfo;
import com.sanyi.faceRecognition.lib.NetSDKLib;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Data{
    public static NetSDKLib netsdk = NetSDKLib.NETSDK_INSTANCE;
    public  Map<String,NetSDKLib.LLong> userLoginMap = new HashMap<>(); //key:ipAndUsername;value:loginHandler
    public  Map<NetSDKLib.LLong,NetSDKLib.LLong> loadLoginMap = new HashMap();//key:attachHandle;value:loginHandler
    public  Map<NetSDKLib.LLong,DeviceStateInfo> deviceStateInfoMap = new HashMap<>(); //key:loginHandler;value:DeviceStateInfo
}
