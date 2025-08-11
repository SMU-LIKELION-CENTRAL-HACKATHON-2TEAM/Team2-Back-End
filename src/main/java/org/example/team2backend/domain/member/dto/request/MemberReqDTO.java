package org.example.team2backend.domain.member.dto.request;

public class MemberReqDTO {

    public record LoginRequestDTO(
            String email,
            String password
    ) {}
}
