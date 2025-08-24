package org.example.team2backend.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.team2backend.global.apiPayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    SAME_VALUE(HttpStatus.BAD_REQUEST, "MEMBER400_1", "변경값이 동일합니다."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST,
            "MEMBER400_2", "입력된 코드가 발급된 코드와 일치하지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER400_3", "비밀번호와 확인 비밀번호가 일치하지 않습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "MEMBER401_1", "이메일 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "회원을 찾을 수 없습니다."),
    CODE_EXPIRED(HttpStatus.NOT_FOUND, "MEMBER404_2", "인증코드가 만료되었습니다."),
    MEMBER_DELETED(HttpStatus.NOT_FOUND, "MEMBER404_3", "탈퇴한 멤버입니다."),
    PASSWORD_UNCHANGED(HttpStatus.CONFLICT, "MEMBER409_1", "새 비밀번호가 기존 비밀번호와 같습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
