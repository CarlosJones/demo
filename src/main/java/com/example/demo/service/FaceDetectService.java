package com.example.demo.service;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class FaceDetectService {
    private static final Logger logger = LoggerFactory.getLogger(FaceDetectService.class);
    //设置APPID/AK/SK
    public static final String APP_ID = "21272723";
    public static final String API_KEY = "gef3tl8twy3RlD5osyEyRteh";
    public static final String SECRET_KEY = "ObnbpRTQyfPF0H2zprubumzSKyNYMAGe";

    public String  getPop(String images,String idCardNumber,String name){
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        // client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 调用接口
        String imageType = "BASE64";

        // 人脸检测
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "NORMAL");
        // options.put("spoofing_control", "LOW");

        // 身份验证
        JSONObject res = client.personVerify(images.toString(), imageType, idCardNumber, name, options);
        logger.info("faceDetectionResult:"+res.toString(2));
        return res.toString();
    }
}
