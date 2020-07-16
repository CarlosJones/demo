package com.sanyi.faceRecognition.service;

import com.sanyi.faceRecognition.entity.DeviceStateInfo;
import com.sanyi.faceRecognition.lib.ToolKits;
import com.sanyi.faceRecognition.lib.NetSDKLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by CarloJones on 2020/7/1.
 */
@Service
public class LoadPictureService {
    private static final Logger logger = LoggerFactory.getLogger(LoadPictureService.class);

    @Autowired
    private Data data;

    public  NetSDKLib.LLong realLoadPicture(NetSDKLib.LLong m_hLoginHandle,int channel, NetSDKLib.fAnalyzerDataCallBack callback) {
        int bNeedPicture = 1; // 是否需要图片

        NetSDKLib.LLong m_hAttachHandle =  Data.netsdk.CLIENT_RealLoadPictureEx(m_hLoginHandle, channel,
                NetSDKLib.EVENT_IVS_ALL, bNeedPicture, callback, null, null);
        if(m_hAttachHandle.longValue() == 0) {
            logger.error("CLIENT_RealLoadPictureEx Failed, Error:" + ToolKits.getErrorCodePrint());
        } else {
            DeviceStateInfo deviceStateInfo = data.deviceStateInfoMap.get(m_hLoginHandle);
            if(null != deviceStateInfo){
                deviceStateInfo.setM_hAttachHandle(m_hAttachHandle);
            }
            data.loadLoginMap.put(m_hAttachHandle,m_hLoginHandle);
            System.out.println("m_hAttachHandle:"+m_hAttachHandle);
            logger.info("通道[" + channel + "]订阅成功！");
        }

        return m_hAttachHandle;
    }

    /**
     * 停止上传智能分析数据－图片
     */
    public static void stopRealLoadPic(NetSDKLib.LLong m_hAttachHandle) {
        if (0 != m_hAttachHandle.longValue()) {
            Data.netsdk.CLIENT_StopLoadPic(m_hAttachHandle);
            m_hAttachHandle.setValue(0);

            logger.info("停止订阅");
        }
    }
}
