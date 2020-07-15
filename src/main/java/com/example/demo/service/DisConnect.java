package com.example.demo.service;

import com.example.demo.lib.NetSDKLib;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by CarloJones on 2020/7/1.
 * 服务器的网络连接断线后，自动调用本类的invoke()接口
 */
public class DisConnect implements NetSDKLib.fDisConnect{
    private static final Logger logger = LoggerFactory.getLogger(DisConnect.class);

    public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
        logger.info("Device[ "+pchDVRIP+" ] Port[ "+nDVRPort+" ] DisConnect!");
        // 断线提示
    }
}
