package org.example.team2backend.domain.member.dto.request;

public record LoginReqDTO(
        String email,
        String password
) {
}
