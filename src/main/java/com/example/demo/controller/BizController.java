package com.example.demo.controller;

import com.example.demo.entity.DetectPersonInfo;
import com.example.demo.entity.DeviceStateInfo;
import com.example.demo.entity.FaceDetect;
import com.example.demo.entity.LoginInfo;
import com.example.demo.lib.NetSDKLib;
import com.example.demo.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        DataAnalyzer dataAnalyzer = DataAnalyzer.getInstance(detectPersonInfo);
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
                            System.out.println("======stopRealLoadPic======1");
                            LoadPictureService.stopRealLoadPic(attachHandle);
                            result = false;
                            System.out.println("======stopRealLoadPic=======2");
                            break;
                        }else{
                            Thread.sleep(100);
                            long cTime = System.currentTimeMillis();
                            if( cTime-sTime > 15000 ){  //35s
                                deviceStateInfo.setFaceDetect(0);
                                deviceStateInfo.setFaceBase64Data(null);
                                System.out.println("======stopRealLoadPic======1");
                                LoadPictureService.stopRealLoadPic(attachHandle);
                                result = false;
                                System.out.println("======stopRealLoadPic=======2");
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
}
