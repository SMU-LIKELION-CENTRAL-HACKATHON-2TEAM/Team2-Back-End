package org.example.team2backend.domain.member.dto.response;

public class MemberResDTO {

    public record UserResponseDTO (
            Long id,
            String content
    ) {}
}
