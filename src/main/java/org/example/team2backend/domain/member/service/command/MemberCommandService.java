package org.example.team2backend.domain.member.service.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.member.converter.MemberConverter;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.entity.Token;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.repository.TokenRepository;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.global.security.auth.CustomUserDetails;
import org.example.team2backend.global.security.auth.CustomUserDetailsService;
import org.example.team2backend.global.security.jwt.JwtDTO;
import org.example.team2backend.global.security.jwt.JwtUtil;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {
    private final MemberRepository memberRepository;

    private final JwtUtil jwtUtil;

    private final UserDetailsService userDetailsService;

    private final TokenRepository tokenRepository;

    public JwtDTO createUser (MemberReqDTO.LoginRequestDTO loginRequestDTO) {
        Member member = MemberConverter.toUser(loginRequestDTO);
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
}
