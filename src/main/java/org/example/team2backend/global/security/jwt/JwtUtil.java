package org.example.team2backend.global.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.entity.Token;
import org.example.team2backend.domain.member.repository.TokenRepository;
import org.example.team2backend.global.security.auth.CustomUserDetails;
import org.example.team2backend.global.security.auth.Roles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final TokenRepository tokenRepository;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token.access-expiration-time}") Long access,
            @Value("${spring.jwt.token.refresh-expiration-time}") Long refresh,
            TokenRepository tokenRepo
    ) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        accessExpMs = access;
        refreshExpMs = refresh;
        tokenRepository = tokenRepo;
    }

    public String getEmail(String token) throws SignatureException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Roles getRoles(String token) throws SignatureException {
        String roleStr = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);  //문자열 형태인 Role을 enum으로 반환
        try {
            return Roles.valueOf(roleStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SignatureException("유효하지 않은 Role 값입니다.");
        }
    }

    public String tokenProvider(CustomUserDetails customUserDetails, Instant expiration) {

        log.info("[ JwtUtil ] 토큰을 새로 생성합니다.");

        //현재 시간 (토큰 생성 시간)
        Instant issuedAt = Instant.now();

        //토큰에 부여할 권한
        String authorities = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(customUserDetails.getUsername())
                .claim("role", authorities)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String createJwtAccessToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(accessExpMs);
        return tokenProvider(customUserDetails, expiration);
    }

    public String createJwtRefreshToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(refreshExpMs);
        String refreshToken = tokenProvider(customUserDetails, expiration);


        tokenRepository.save(Token.builder()
                .email(customUserDetails.getUsername())
                .token(refreshToken)
                .build()
        );


        return refreshToken;
    }

    public JwtDTO reissueToken(String refreshToken) throws SignatureException {

        CustomUserDetails userDetails = new CustomUserDetails(
                getEmail(refreshToken),
                null,
                getRoles(refreshToken)
        );
        log.info("[ JwtUtil ] 새로운 토큰을 재발급 합니다.");

        //재발급
        return new JwtDTO(
                createJwtAccessToken(userDetails),
                createJwtRefreshToken(userDetails)
        );

    }

    public String resolveAccessToken(HttpServletRequest request) {
        log.info("[ JwtUtil ] 헤더에서 토큰을 추출합니다.");
        String tokenFromHeader = request.getHeader("Authorization");

        //헤더에 토큰이 없거나, beartoken이 아닐 때
        if (tokenFromHeader == null || !tokenFromHeader.startsWith("Bearer ")){
            log.warn("[ JwtUtil ] Request Header 에 토큰이 존재하지 않습니다.");
            return null;
        }

        log.info(" [ JwtUtil ] 헤더에 토큰이 존재합니다.");

        return tokenFromHeader.split(" ")[1]; //Bearer 와 분리
    }

    public void validateToken(String token) {
        log.info("[ JwtUtil ] 토큰의 유효성을 검증합니다.");
        try {
            long seconds = 3 * 60;
            boolean isExpired = Jwts
                    .parser()
                    .clockSkewSeconds(seconds)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
            // 만료된 경우 예외 발생
            if (isExpired) {
                log.warn("만료된 JWT 토큰입니다.");
                throw new ExpiredJwtException(null, null, "JWT 토큰이 만료되었습니다.");
            }
        } catch (ExpiredJwtException e) {
            // JWT 라이브러리 내부에서 토큰 만료가 감지된 경우
            log.warn("[ JwtUtil ] JWT 토큰이 만료되었습니다.");
            throw new ExpiredJwtException(null, null, "만료된 JWT 토큰입니다.");
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new SecurityException("잘못된 토큰입니다");

        }
    }

}
