package org.example.team2backend.global.security.jwt;

public record JwtDTO(
        String jwtAccessToken,
        String jwtRefreshToken
) {
}
