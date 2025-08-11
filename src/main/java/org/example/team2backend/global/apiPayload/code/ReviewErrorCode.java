package org.example.team2backend.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode{

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404", "존재하지 않은 리뷰입니다."),
    REVIEW_IMAGE_LIMIT(HttpStatus.BAD_REQUEST, "REVIEW400", "이미지는 최대 3개까지만 업로드할 수 있습니다."),
    REVIEW_ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "REVIEW401", "해당 리뷰에 대한 권한이 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
