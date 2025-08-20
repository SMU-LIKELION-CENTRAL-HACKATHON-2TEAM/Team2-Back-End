package org.example.team2backend.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.team2backend.global.apiPayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberRouteErrorCode implements BaseErrorCode {

    MEMBER_ROUTE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_ROUTE400_1", "해당 테이블을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
