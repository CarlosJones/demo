package com.example.demo.service;

import com.example.demo.lib.ToolKits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.File;
import com.example.demo.lib.NetSDKLib;

@Service
public class InitClientService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(InitClientService.class);

    private static boolean bInit   = false;
    private static boolean bLogopen = false;
    // 设备断线通知回调
    private static DisConnect disConnect = new DisConnect();
    // 网络连接恢复
    private static HaveReConnect haveReConnect = HaveReConnect.getInstance();

    public boolean init(NetSDKLib.fDisConnect disConnect, NetSDKLib.fHaveReConnect haveReConnect) {

        bInit = Data.netsdk.CLIENT_Init(disConnect, null);
        if (!bInit) {
            logger.error("Initialize SDK failed");
            return false;
        }

        // 设置断线重连成功回调函数
        Data.netsdk.CLIENT_SetAutoReconnect(HaveReConnect.getInstance(), null);

        //打开日志，可选
        NetSDKLib.LOG_SET_PRINT_INFO setLog = new NetSDKLib.LOG_SET_PRINT_INFO();
        File path = new File("./sdklog/");
        if (!path.exists()) {
            path.mkdir();
        }
        String logPath = path.getAbsoluteFile().getParent() + "\\sdklog\\" + ToolKits.getDate() + ".log";
        setLog.nPrintStrategy = 0;
        setLog.bSetFilePath = 1;
        System.arraycopy(logPath.getBytes(), 0, setLog.szLogFilePath, 0, logPath.getBytes().length);
        System.out.println(logPath);
        setLog.bSetPrintStrategy = 1;
        bLogopen = Data.netsdk.CLIENT_LogOpen(setLog);
        if(!bLogopen ) {
            logger.error("Failed to open NetSDK log");
        }

        return bInit;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        init(disConnect, haveReConnect);
    }
}
