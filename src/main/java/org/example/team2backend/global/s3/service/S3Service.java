package org.example.team2backend.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.example.team2backend.global.apiPayload.code.S3ErrorCode;
import org.example.team2backend.global.apiPayload.exception.S3Exception;
import org.example.team2backend.global.s3.data.S3ConfigData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    private final S3ConfigData s3ConfigData;

    public String upload(MultipartFile file, String domain) {
        String key = domain + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(s3ConfigData.getBucket(), key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.UPLOAD_FAILED);
        }
        return key;
    }

    public String getFileUrl(String key) {
        return amazonS3.getUrl(s3ConfigData.getBucket(), key).toString();
    }

    public void deleteFile(String key) {
        try {
            amazonS3.deleteObject(s3ConfigData.getBucket(), key);
        } catch (AmazonS3Exception e) {
            throw new S3Exception(S3ErrorCode.DELETE_FAILED);
        }
    }

    public boolean doesFileExist(String key) {
        return amazonS3.doesObjectExist(s3ConfigData.getBucket(), key);
    }
}
