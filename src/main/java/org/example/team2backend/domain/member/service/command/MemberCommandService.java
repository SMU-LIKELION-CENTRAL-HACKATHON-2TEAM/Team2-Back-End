package org.example.team2backend.domain.member.service.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.converter.MemberConverter;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.entity.Token;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.repository.TokenRepository;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.global.apiPayload.code.AuthErrorCode;
import org.example.team2backend.global.security.auth.AuthException;
import org.example.team2backend.global.security.auth.CustomUserDetails;
import org.example.team2backend.global.security.auth.CustomUserDetailsService;
import org.example.team2backend.global.security.jwt.JwtDTO;
import org.example.team2backend.global.security.jwt.JwtUtil;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.SignatureException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {
    private final MemberRepository memberRepository;

    private final JwtUtil jwtUtil;

    private final UserDetailsService userDetailsService;

    private final TokenRepository tokenRepository;

    public JwtDTO createUser (MemberReqDTO.LoginRequestDTO loginRequestDTO) {
        Member member = MemberConverter.toMember(loginRequestDTO);
        if (memberRepository.findByEmail(loginRequestDTO.email()).isEmpty()) {
            memberRepository.save(member);
        }
        return createJwt(member);
    }

    //Jwt 생성
    public JwtDTO createJwt(Member member) {

        CustomUserDetailsService customUserDetailsService =
                new CustomUserDetailsService(memberRepository);

        CustomUserDetails newUserDetails =
                (CustomUserDetails)customUserDetailsService.loadUserByUsername(member.getEmail());

        //access 토큰 발급
        String accessToken = jwtUtil.createJwtAccessToken(newUserDetails);

        //refresh 토큰 발급
        String refreshToken = jwtUtil.createJwtRefreshToken(newUserDetails);
        //리프레시 토큰을 저장하기 위해 token 엔티티를 만든 뒤, refresh 토큰을 담아 저장
        Token token = Token.builder()
                .email(member.getEmail())
                .token(refreshToken)
                .build();

        //멤버와의 연관관계
        token.setMember(member);

        tokenRepository.save(token);

        return new JwtDTO(
                accessToken,
                refreshToken
        );
    }

    public JwtDTO reissueToken(JwtDTO jwtDto) throws SignatureException {

        log.info("[ Auth Service ] 토큰 재발급을 시작합니다.");
        //토큰 파싱
        String accessToken = jwtDto.jwtAccessToken();
        String refreshToken = jwtDto.jwtRefreshToken();

        //Access Token 으로부터 사용자 Email 추출
        String email = jwtUtil.getEmail(refreshToken); // **수정부분**
        log.info("[ Auth Service ] Email ---> {}", email);

        //Access Token 에서의 Email 로 부터 DB 에 저장된 Refresh Token 가져오기
        Token refreshTokenByDB = tokenRepository.findByEmail(email).orElseThrow(
                () -> new AuthException(AuthErrorCode.UNAUTHORIZED_401)
        );

        //Refresh Token 이 유효한지 검사
        jwtUtil.validateToken(refreshToken);

        log.info("[ Auth Service ] Refresh Token 이 유효합니다.");

        //만약 DB 에서 찾은 Refresh Token 과 파라미터로 온 Refresh Token 이 일치한다면 새로운 토큰 발급
        if (refreshTokenByDB.getToken().equals(refreshToken)) {
            log.info("[ Auth Service ] 토큰을 재발급합니다.");
            return jwtUtil.reissueToken(refreshToken);
        } else {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED_401);
        }

    }
}
