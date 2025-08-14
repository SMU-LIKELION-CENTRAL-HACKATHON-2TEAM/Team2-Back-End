package org.example.team2backend.domain.member.dto.response;

public class MemberResDTO {

    public record MemberResponseDTO (
            String email,
            String nickname
    ) {}
}
