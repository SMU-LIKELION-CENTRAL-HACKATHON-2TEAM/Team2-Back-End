package org.example.team2backend.global.apiPayload.exception;

import lombok.Getter;
import org.example.team2backend.global.apiPayload.code.BaseErrorCode;

@Getter
public class CustomException extends RuntimeException{

    private final BaseErrorCode code;

    public CustomException(BaseErrorCode errorCode) {
        this.code = errorCode;
    }
}
