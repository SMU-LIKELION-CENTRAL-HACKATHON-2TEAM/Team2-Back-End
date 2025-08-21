package org.example.team2backend.domain.member.converter;

import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.entity.EmailVerification;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.entity.SocialType;
import org.example.team2backend.global.security.auth.Roles;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class MemberConverter {

    public static Member toMember(MemberReqDTO.SignUpRequestDTO signUpRequestDTO, PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(signUpRequestDTO.password());
        return Member.builder()
                .email(signUpRequestDTO.email())
                .password(encodedPassword)
                .nickname(signUpRequestDTO.nickname())
                .role(Roles.ROLE_USER)
                .socialType(SocialType.CUSTOM)
                .build();
    }

    public static EmailVerification toEmailVerification(String email, String code){
        return EmailVerification.builder()
                .email(email)
                .code(code)
                .verified(false)
                //만료기한 설정(5분)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }
}
