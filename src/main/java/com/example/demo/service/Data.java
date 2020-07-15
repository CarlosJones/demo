package com.example.demo.service;

import com.example.demo.entity.DeviceStateInfo;
import com.example.demo.lib.NetSDKLib;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Data{
    public static NetSDKLib netsdk = NetSDKLib.NETSDK_INSTANCE;
    public static Map<String,NetSDKLib.LLong> userLoginMap = new HashMap<>(); //key:ipAndUsername;value:loginHandler
    public static Map<NetSDKLib.LLong,NetSDKLib.LLong> loadLoginMap = new HashMap();//key:attachHandle;value:loginHandler
    public static Map<NetSDKLib.LLong,DeviceStateInfo> deviceStateInfoMap = new HashMap<>(); //key:loginHandler;value:DeviceStateInfo
}
