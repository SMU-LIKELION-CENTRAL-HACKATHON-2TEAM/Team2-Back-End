package org.example.team2backend.domain.route.exception;

import org.example.team2backend.global.apiPayload.code.BaseErrorCode;
import org.example.team2backend.global.apiPayload.exception.CustomException;

public class RouteException extends CustomException {
    public RouteException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
