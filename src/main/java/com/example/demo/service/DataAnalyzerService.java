//package com.example.demo.service;
//
//import com.example.demo.common.Base64;
//import com.example.demo.entity.DetectPersonInfo;
//import com.example.demo.entity.FaceDetect;
//import com.example.demo.lib.NetSDKLib;
//import com.sun.jna.Pointer;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.UUID;
//
//
//public class DataAnalyzerService implements NetSDKLib.fAnalyzerDataCallBack{
//    private static final Logger logger = LoggerFactory.getLogger(DataAnalyzerService.class);
//
//    private FaceDetect faceDetect = null;
//
//    private FaceDetectService faceDetectService;
//
//    private DetectPersonInfo personInfo;
//
//    public DataAnalyzerService(FaceDetectService faceDetectService, DetectPersonInfo personInfo) {
//        this.faceDetectService = faceDetectService;
//        this.personInfo = personInfo;
//    }
//
//    //    private DataAnalyzerService() {}
////
////    private static class AnalyzerDataCBHolder {
////        private static final DataAnalyzerService instance = new DataAnalyzerService();
////    }
////
////    public static DataAnalyzerService getInstance() {
////        return DataAnalyzerService.AnalyzerDataCBHolder.instance;
////    }
//
//    public int invoke(NetSDKLib.LLong lAnalyzerHandle, int dwAlarmType,
//                      Pointer pAlarmInfo, Pointer pBuffer, int dwBufSize,
//                      Pointer dwUser, int nSequence, Pointer reserved)
//    {
//        // 保存图片，获取图片缓存
//        try {
////            saveFaceDetectPic(pBuffer, dwBufSize);
//            faceDetect = detectFace(pBuffer,dwBufSize,personInfo);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // 释放内存
//        System.gc();
//
//        return 0;
//    }
//
//    /**
//     * 保存人脸检测事件图片
//     * @param pBuffer 抓拍图片信息
//     * @param dwBufSize 抓拍图片大小
//     */
//    public void saveFaceDetectPic(Pointer pBuffer, int dwBufSize) throws Exception {
//        String path = "设备编号";
//        String bucketName = "orgcodegkey";    //医疗机构编码
//
//        if (pBuffer == null || dwBufSize <= 0) {
//            return;
//        }
//
//        ///->保存人脸图
//        String strPersonPicPathName = path + "/" + UUID.randomUUID() + "_Person.jpg";
//        byte[] bufferPerson = pBuffer.getByteArray(0, dwBufSize);
//        ByteArrayInputStream byteArrInputPerson = new ByteArrayInputStream(bufferPerson);
//
//        try {
//            FileUploadService.uploadPicture(bucketName,strPersonPicPathName,byteArrInputPerson,dwBufSize,"application/octet-stream");
//        } catch (IOException e2) {
//            e2.printStackTrace();
//        }
//    }
//
//    public FaceDetect detectFace(Pointer pBuffer, int dwBufSize, DetectPersonInfo personInfo){
//        FaceDetect faceDetect = new FaceDetect();
//
//        if (pBuffer == null || dwBufSize <= 0) {
//            return null;
//        }
//
//        byte[] bufferPerson = pBuffer.getByteArray(0, dwBufSize);
//        String base64FaceData = Base64.getEncoder().encodeToString(bufferPerson);
//        faceDetect.setFaceData(base64FaceData);
//        String jsonResult = faceDetectService.getPop(base64FaceData,personInfo.getIdCard(),personInfo.getUserName());
//        JSONObject faceDetectJsonResult = new JSONObject(jsonResult);
//        Object reultObj = faceDetectJsonResult.get("result");
//        if(!"null".equals(reultObj.toString())){
//            JSONObject result = faceDetectJsonResult.getJSONObject("result");
//            int score = result.getInt("score");
//            faceDetect.setSimilarityDegree(score);
//        }
//
//        return faceDetect;
//    }
//}
