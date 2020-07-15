package com.example.demo.service;

import com.example.demo.common.Base64;
import com.example.demo.entity.DetectPersonInfo;
import com.example.demo.entity.DeviceStateInfo;
import com.example.demo.lib.NetSDKLib;
import com.example.demo.lib.ToolKits;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataAnalyzer implements NetSDKLib.fAnalyzerDataCallBack{
    private final Logger logger = LoggerFactory.getLogger(DataAnalyzer.class);

    private volatile static DataAnalyzer dataAnalyzerInstance = null;
    private DetectPersonInfo personInfo;
    // 用于人脸检测
    private int groupId = 0;

    private DataAnalyzer(DetectPersonInfo personInfo) {
        this.personInfo = personInfo;
    }

    public static DataAnalyzer getInstance(DetectPersonInfo personInfo) {
        if (dataAnalyzerInstance == null) {
            synchronized (DataAnalyzer.class) {
                if (dataAnalyzerInstance == null) {
                    dataAnalyzerInstance = new DataAnalyzer(personInfo);
                }
            }
        }
        return dataAnalyzerInstance;
    }

    public int invoke(NetSDKLib.LLong lAnalyzerHandle, int dwAlarmType,
                      Pointer pAlarmInfo, Pointer pBuffer, int dwBufSize,
                      Pointer dwUser, int nSequence, Pointer reserved)
    {

        NetSDKLib.DEV_EVENT_FACEDETECT_INFO msg = new NetSDKLib.DEV_EVENT_FACEDETECT_INFO();

        ToolKits.GetPointerData(pAlarmInfo, msg);

        try {
            detectFace(lAnalyzerHandle,pBuffer,dwBufSize,personInfo,msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 释放内存
        msg = null;
        System.gc();

        return 0;
    }

    public void detectFace(NetSDKLib.LLong lAnalyzerHandle,Pointer pBuffer, int dwBufSize, DetectPersonInfo personInfo,
                           NetSDKLib.DEV_EVENT_FACEDETECT_INFO faceDetectInfo){

        if (pBuffer == null || dwBufSize <= 0) {
            return;
        }

        // 小图的 stuObject.nRelativeID 来匹配大图的 stuObject.nObjectID，来判断是不是 一起的图片
        if(groupId != faceDetectInfo.stuObject.nRelativeID) {   ///->保存全景图
            groupId = faceDetectInfo.stuObject.nObjectID;
        } else if(groupId == faceDetectInfo.stuObject.nRelativeID) {   ///->保存人脸图
            if (faceDetectInfo.stuObject.stPicInfo != null) {
                byte[] bufferPerson = pBuffer.getByteArray(0, dwBufSize);
                if(null != bufferPerson && bufferPerson.length >0){            //protect getPop(),very expensive
                    System.out.println("picLoad:"+lAnalyzerHandle);
                    NetSDKLib.LLong loginHandle = Data.loadLoginMap.get(lAnalyzerHandle);
                    System.out.println("picLogin:"+loginHandle);
                    if(null != loginHandle && loginHandle.longValue() != 0){
                        DeviceStateInfo deviceStateInfo = Data.deviceStateInfoMap.get(loginHandle);
                        String base64FaceData = Base64.getEncoder().encodeToString(bufferPerson);
                        System.out.println("image:"+base64FaceData);
                        deviceStateInfo.setFaceBase64Data(base64FaceData);
                        System.out.println("=================baidu face detect API================");
                        logger.info("=================baidu face detect API================");
//                            String jsonResult = BizController.this.faceDetectService.getPop(base64FaceData,personInfo.getIdCard(),personInfo.getUserName());
//                            logger.info("faceDetectResult:"+jsonResult);
//                            JSONObject faceDetectJsonResult = new JSONObject(jsonResult);
//                            System.out.println("lAnalyzerHandle" + faceDetectJsonResult.toString());
//                            if(faceDetectJsonResult.has("result")){
//                                Object resultObj = faceDetectJsonResult.get("result");
//                                if(!"null".equals(resultObj.toString())){
//                                    JSONObject result = faceDetectJsonResult.getJSONObject("result");
//                                    if(result.has("score")){
//                                        float score = result.getFloat("score");
//                                        deviceStateInfo.setFaceDetect(score);
//                                    }
//                                }
//                            }
                    }
                }
            }
        }
    }
}
