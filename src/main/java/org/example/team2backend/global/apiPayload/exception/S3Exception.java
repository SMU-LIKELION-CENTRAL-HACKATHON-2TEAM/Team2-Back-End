package org.example.team2backend.global.apiPayload.exception;

import org.example.team2backend.global.apiPayload.code.S3ErrorCode;

public class S3Exception extends CustomException{
    public S3Exception(S3ErrorCode errorCode) {
        super(errorCode);
    }
}
