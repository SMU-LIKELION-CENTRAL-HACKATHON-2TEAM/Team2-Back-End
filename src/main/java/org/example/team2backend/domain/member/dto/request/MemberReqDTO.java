package org.example.team2backend.domain.member.dto.request;

public class MemberReqDTO {

    public record SignUpRequestDTO(
            String email,
            String password,
            String confirmPassword,
            String nickname
    ) {}

    public record LoginRequestDTO(
            String email,
            String password
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
            String email,
            String code
    ) {}
}
