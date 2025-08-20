package org.example.team2backend.global.apiPayload.exception;

import org.example.team2backend.global.apiPayload.code.ReviewErrorCode;

public class ReviewException extends CustomException{
    public ReviewException(ReviewErrorCode errorCode) {super(errorCode);}
}
