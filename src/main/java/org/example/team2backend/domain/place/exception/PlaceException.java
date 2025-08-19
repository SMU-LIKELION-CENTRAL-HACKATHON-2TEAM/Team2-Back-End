package org.example.team2backend.domain.place.exception;

import org.example.team2backend.global.apiPayload.code.BaseErrorCode;
import org.example.team2backend.global.apiPayload.exception.CustomException;

public class PlaceException extends CustomException {
    public PlaceException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
