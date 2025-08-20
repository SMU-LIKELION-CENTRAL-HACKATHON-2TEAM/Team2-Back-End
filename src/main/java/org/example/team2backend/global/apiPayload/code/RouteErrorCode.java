package org.example.team2backend.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RouteErrorCode implements BaseErrorCode{

    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTE400", "존재하지 않은 루트입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
