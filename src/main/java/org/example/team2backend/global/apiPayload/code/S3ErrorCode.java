package org.example.team2backend.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum S3ErrorCode implements BaseErrorCode{

    UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "S3400", "S3 업로드 실패"),
    DELETE_FAILED(HttpStatus.BAD_REQUEST, "S3400", "S3 이미지 삭제 실패")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
