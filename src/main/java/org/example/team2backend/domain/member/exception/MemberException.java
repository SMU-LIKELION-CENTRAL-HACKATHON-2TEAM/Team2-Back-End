package org.example.team2backend.domain.member.exception;

import org.example.team2backend.global.apiPayload.code.BaseErrorCode;
import org.example.team2backend.global.apiPayload.exception.CustomException;

public class MemberException extends CustomException {
    public MemberException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
