package org.example.team2backend.global.security.handler;

import ch.qos.logback.core.status.ErrorStatus;
import lombok.Getter;

@Getter
public class AuthHandler extends RuntimeException {

    private final ErrorStatus errorStatus;

    public AuthHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage()); // Optional: 에러 메시지 전달
        this.errorStatus = errorStatus;
    }

}
