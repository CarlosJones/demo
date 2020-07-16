package com.sanyi.faceRecognition.service;

import com.sanyi.faceRecognition.common.Base64;
import com.sanyi.faceRecognition.entity.DeviceStateInfo;
import com.sanyi.faceRecognition.lib.NetSDKLib;
import com.sanyi.faceRecognition.lib.ToolKits;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallBackService implements NetSDKLib.fAnalyzerDataCallBack {
    private final Logger logger = LoggerFactory.getLogger(CallBackService.class);

    // 用于人脸检测
    private int groupId = 0;
    private boolean captureFace = false;
    @Autowired
    private Data data;

    public CallBackService() {}

    public int invoke(NetSDKLib.LLong lAnalyzerHandle, int dwAlarmType,
                      Pointer pAlarmInfo, Pointer pBuffer, int dwBufSize,
                      Pointer dwUser, int nSequence, Pointer reserved)
    {
        captureFace = false;

        if (pBuffer == null || dwBufSize <= 0) {
            return 0;
        }

        try {
            NetSDKLib.DEV_EVENT_FACEDETECT_INFO faceDetectInfo = new NetSDKLib.DEV_EVENT_FACEDETECT_INFO();
            ToolKits.GetPointerData(pAlarmInfo,faceDetectInfo);

            NetSDKLib.LLong loginHandle = data.loadLoginMap.get(lAnalyzerHandle);
            if(null != loginHandle && loginHandle.longValue() != 0 && null != pBuffer) {
                if(groupId != faceDetectInfo.stuObject.nRelativeID) {   ///->保存全景图
                    groupId = faceDetectInfo.stuObject.nObjectID;
                } else if(groupId == faceDetectInfo.stuObject.nRelativeID) {   ///->保存人脸图
                    if (faceDetectInfo.stuObject.stPicInfo != null) {
                        DeviceStateInfo deviceStateInfo = data.deviceStateInfoMap.get(loginHandle);
                        byte[] bufferPerson = pBuffer.getByteArray(0, dwBufSize);
                        String base64FaceData = Base64.getEncoder().encodeToString(bufferPerson);
                        System.out.println("image:" + base64FaceData);
                        deviceStateInfo.setFaceBase64Data(base64FaceData);
                        logger.info("saveBase64Photo,photoSize:"+base64FaceData.length());
                        captureFace = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(captureFace){
                data.loadLoginMap.remove(lAnalyzerHandle);
                System.out.println("removeRealLoadPic:" + lAnalyzerHandle);
            }
        }

        System.gc();
        return 0;
    }

}
