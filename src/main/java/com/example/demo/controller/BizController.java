package com.example.demo.controller;

import com.example.demo.common.Base64;
import com.example.demo.entity.DetectPersonInfo;
import com.example.demo.entity.DeviceStateInfo;
import com.example.demo.entity.FaceDetect;
import com.example.demo.entity.LoginInfo;
import com.example.demo.lib.NetSDKLib;
import com.example.demo.lib.ToolKits;
import com.example.demo.service.*;
import com.sun.jna.Pointer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BizController {
    private static final Logger logger = LoggerFactory.getLogger(BizController.class);

    @Autowired
    private LoginService loginService;
    @Autowired
    private FaceDetectService faceDetectService;

    private Map<String, LoginInfo> loginMap = new HashMap<>();


    public BizController() {
        LoginInfo df = new LoginInfo("192.168.1.10",37777,"admin","admin888");
        loginMap.put("df",df);
        LoginInfo cao = new LoginInfo("192.168.1.66",37777,"admin","admin888");
        loginMap.put("cao",cao);
    }

    /**
     * 功能描述:处理订阅请求<br/>
     * @param doctorName
     * @return
     */
    @GetMapping(value="/loadPic")
    public FaceDetect loadPic(@RequestParam String doctorName,@RequestParam String idCard,@RequestParam String userName) throws Exception {
        DetectPersonInfo detectPersonInfo = new DetectPersonInfo(userName,idCard);
        DataAnalyzer dataAnalyzer = new DataAnalyzer(detectPersonInfo);
        FaceDetect faceDetect = new FaceDetect();
        boolean result = false;
        NetSDKLib.LLong loginResult = null;      //login handler
        DeviceStateInfo deviceStateInfo = null;
        LoginInfo loginInfo = loginMap.get(doctorName);
        if(null != loginInfo){
            String ipAndUsername = loginInfo.getIp() + ":" + loginInfo.getUserName();
            NetSDKLib.LLong loginHandle = Data.userLoginMap.get(ipAndUsername);
            if(null != loginHandle){
                deviceStateInfo = Data.deviceStateInfoMap.get(loginHandle);
                if(null == deviceStateInfo ){
                    loginResult = loginService.login(loginInfo.getIp(),loginInfo.getPort(),loginInfo.getUserName(),loginInfo.getPwd());
                    logger.info("[ " +ipAndUsername+ " ] Login result is : " + loginResult);
                }else{
                    NetSDKLib.LLong m_hLoginHandle = deviceStateInfo.getM_hLoginHandle();
                    if(m_hLoginHandle.longValue() == 0){
                        loginResult = loginService.login(loginInfo.getIp(),loginInfo.getPort(),loginInfo.getUserName(),loginInfo.getPwd());
                        logger.info("[ " +ipAndUsername+ " ] Login result is : " + loginResult.longValue());
                    }else{
                        loginResult = deviceStateInfo.getM_hLoginHandle();
                    }
                }
            }else{
                loginResult = loginService.login(loginInfo.getIp(),loginInfo.getPort(),loginInfo.getUserName(),loginInfo.getPwd());
                logger.info("[ " +ipAndUsername+ " ] Login result is : " + loginResult);
            }

            //maybe refreshed
            deviceStateInfo = Data.deviceStateInfoMap.get(loginResult);
            if(null != deviceStateInfo){
                NetSDKLib.LLong m_hLoginHandle = deviceStateInfo.getM_hLoginHandle();
                NetSDKLib.LLong m_hAttachHandle = deviceStateInfo.getM_hAttachHandle();
                if(m_hLoginHandle.longValue() != 0 && m_hAttachHandle.longValue() == 0){
                    NetSDKLib.LLong attachHandle = LoadPictureService.realLoadPicture(m_hLoginHandle,0,dataAnalyzer);
                    deviceStateInfo.setM_hAttachHandle(attachHandle);
                    result = (attachHandle.longValue() != 0);
                    logger.info("[ " +ipAndUsername+ " ] attach result is : " + result);
                    long sTime = System.currentTimeMillis();
                    while(result){
                        deviceStateInfo = Data.deviceStateInfoMap.get(loginResult);
                        if(null != deviceStateInfo && null != deviceStateInfo.getFaceBase64Data()){
                            faceDetect.setFaceData(deviceStateInfo.getFaceBase64Data());
                            faceDetect.setSimilarityDegree(deviceStateInfo.getFaceDetect());
                            deviceStateInfo.setFaceDetect(0);
                            deviceStateInfo.setFaceBase64Data(null);
                            LoadPictureService.stopRealLoadPic(attachHandle);
                            result = false;
                            System.out.println("======stopRealLoadPic=======");
                            break;
                        }else{
                            Thread.sleep(100);
                            long cTime = System.currentTimeMillis();
                            if( cTime-sTime > 15000 ){  //15s
                                break;
                            }
                        }
                    }
                }
            }

        }
        System.gc();
        return faceDetect;
    }

    private class DataAnalyzer implements NetSDKLib.fAnalyzerDataCallBack{
        private final Logger logger = LoggerFactory.getLogger(DataAnalyzer.class);
        private DetectPersonInfo personInfo;
        // 用于人脸检测
        private int groupId = 0;

        public DataAnalyzer(DetectPersonInfo personInfo) {
            this.personInfo = personInfo;
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
}
