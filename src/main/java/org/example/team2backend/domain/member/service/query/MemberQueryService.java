package org.example.team2backend.domain.member.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.dto.response.MemberResDTO;
import org.example.team2backend.domain.member.entity.EmailVerification;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.exception.MemberErrorCode;
import org.example.team2backend.domain.member.exception.MemberException;
import org.example.team2backend.domain.member.repository.EmailVerificationRepository;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.global.apiPayload.code.GeneralErrorCode;
import org.example.team2backend.global.apiPayload.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.team2backend.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static org.example.team2backend.domain.member.exception.MemberErrorCode.VERIFICATION_CODE_MISMATCH;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    //회원 정보 조회
    public MemberResDTO.MemberResponseDTO showMemberInfo(
            String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        return new MemberResDTO.MemberResponseDTO(email, member.getNickname());
    }

    //인증 코드 일치 여부 검사
    public void verifyCode(MemberReqDTO.VerifyRequestDTO verifyRequestDTO) {

        EmailVerification emailVerification =
                emailVerificationRepository.findByEmailAndCode(verifyRequestDTO.email(), verifyRequestDTO.code()).orElseThrow(() -> new CustomException(GeneralErrorCode.NOT_FOUND_404));

        //인증코드가 일치하는 지 검사
        if (!emailVerification.getCode().equals(verifyRequestDTO.code())) {
            throw new CustomException(VERIFICATION_CODE_MISMATCH);
        }
        //인증코드가 만료되었는 지 검사
        if (emailVerification.isExpired()) {
            throw new MemberException(MemberErrorCode.CODE_EXPIRED);
        }
        //인증 여부 true로 변경
        else{
            emailVerification.verify();
        }
    }
}
