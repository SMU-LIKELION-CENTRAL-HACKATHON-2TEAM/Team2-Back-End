package org.example.team2backend.global.security.auth;

import org.example.team2backend.global.apiPayload.code.BaseErrorCode;
import org.example.team2backend.global.apiPayload.exception.CustomException;

public class AuthException extends CustomException {

    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
