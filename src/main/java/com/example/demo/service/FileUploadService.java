package com.example.demo.service;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by CarloJones on 2020/7/1.
 */
public class FileUploadService {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    public static void uploadPicture(String bucketName, String picName, InputStream stream, long size, String contentType) throws Exception{
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
//            MinioClient minioClient = new MinioClient("http://xxxxxxx", "xxxxxxx", "xxxxxxx");
            MinioClient minioClient = new MinioClient("http://xxxxxxx", "xxxxxxx", "xxxxxxx");

            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(bucketName);
            if(isExist) {
                logger.info("Bucket already exists.");
            } else {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket(bucketName);
            }

            // 使用putObject上传一个文件到存储桶中
            minioClient.putObject(bucketName,picName, stream,size,contentType);
            logger.info(picName + " is successfully uploaded.");
        } catch(MinioException e) {
            logger.error("Error occurred: " + e);
        }
    }
}
