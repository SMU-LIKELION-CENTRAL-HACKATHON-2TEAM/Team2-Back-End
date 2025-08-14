package org.example.team2backend.domain.member.service.query;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.dto.response.MemberResDTO;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.global.apiPayload.exception.CustomException;
import org.example.team2backend.global.security.auth.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import static org.example.team2backend.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberResDTO.MemberResponseDTO showMemberInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        return new MemberResDTO.MemberResponseDTO(email, member.getNickname());


    }
}
