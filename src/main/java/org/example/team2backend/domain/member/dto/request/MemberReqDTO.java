package org.example.team2backend.domain.member.dto.request;

public class MemberReqDTO {

    public record SignUpRequestDTO(
            String email,
            String password,
            String confirmPassword,
            String nickname
    ) {}

    public record UpdateNicknameDTO(
            String newNickname
    ) {}

    public record UpdatePasswordDTO(
            String newPassword
    ) {}

    public record MailRequestDTO(
            String email
    ) {}

    public record VerifyRequestDTO(
            String userCode, //입력된 코드
            String verificationCode //발급된 코드
    ) {}
}
