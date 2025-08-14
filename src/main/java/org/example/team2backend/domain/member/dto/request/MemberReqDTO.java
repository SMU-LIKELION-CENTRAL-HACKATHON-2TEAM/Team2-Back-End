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
}
