package org.example.team2backend.global.s3.data;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "s3")
public class S3ConfigData {
    private String bucket;
    private String region;
    private String accessKey;
    private String secretKey;
    private String baseUrl;

    @PostConstruct
    public void init() {
        this.baseUrl = String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);
    }
}