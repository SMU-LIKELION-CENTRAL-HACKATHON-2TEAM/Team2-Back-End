package org.example.team2backend.domain.place.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PlaceErrorCode {

    PLACE_NOT_FOUND(HttpStatus.BAD_REQUEST, "PLACE404_1", "해당하는 주소를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
