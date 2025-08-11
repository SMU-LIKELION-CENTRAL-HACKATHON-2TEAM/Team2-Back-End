package org.example.team2backend.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode{

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW400", "존재하지 않은 리뷰입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
