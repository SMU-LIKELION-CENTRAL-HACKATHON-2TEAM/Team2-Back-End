package org.example.team2backend.domain.route.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.team2backend.global.apiPayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RouteErrorCode implements BaseErrorCode {

    ROUTE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ROUTE400_1", "입력 받은 루트가 이미 존재합니다."),
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTE404_1", "해당하는 루트를 찾을 수 없습니다."),
    ROUTE_NOT_ENOUGH(HttpStatus.NOT_FOUND, "ROUTE404_2", "루트의 개수가 부족합니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
