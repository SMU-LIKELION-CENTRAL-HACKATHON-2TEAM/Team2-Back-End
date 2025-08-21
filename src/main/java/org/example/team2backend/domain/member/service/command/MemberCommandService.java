package org.example.team2backend.domain.member.service.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.converter.MemberConverter;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.repository.TokenRepository;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.global.apiPayload.code.AuthErrorCode;
import org.example.team2backend.global.apiPayload.exception.CustomException;
import org.example.team2backend.global.security.auth.AuthException;
import org.example.team2backend.global.security.auth.CustomUserDetails;
import org.example.team2backend.global.security.auth.CustomUserDetailsService;
import org.example.team2backend.global.security.jwt.JwtDTO;
import org.example.team2backend.global.security.jwt.JwtUtil;
import org.springframework.stereotype.Service;

import java.security.SignatureException;

import static org.example.team2backend.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static org.example.team2backend.domain.member.exception.MemberErrorCode.PASSWORD_UNCHANGED;
import static org.example.team2backend.domain.member.exception.MemberErrorCode.SAME_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {
    private final MemberRepository memberRepository;

    private final JwtUtil jwtUtil;

    private final TokenRepository tokenRepository;

    private final CustomUserDetailsService userDetailsService;

    //회원 가입(생성)
    public void createUser(MemberReqDTO.SignUpRequestDTO signUpRequestDTO) {
        Member member = MemberConverter.toMember(signUpRequestDTO);
        //입력한 패스워드가 확인용 패스워드와 다르다면 예외 발생
        if (!signUpRequestDTO.password().equals(signUpRequestDTO.confirmPassword())) {
            throw new AuthException(AuthErrorCode.BAD_REQUEST_400);
        }
        //이메일이 db에 없다면 추가 이미 있으면 예외 발생
        if (memberRepository.findByEmail(signUpRequestDTO.email()).isEmpty()) {
            memberRepository.save(member);
        } else {
            throw new AuthException(AuthErrorCode.BAD_REQUEST_400);
        }
    }

    //refreshToken을 바탕으로 토큰 재발급
    public JwtDTO reissueToken(JwtDTO jwtDTO) throws SignatureException {

        String oldRefreshToken = jwtDTO.jwtRefreshToken();

        if (!tokenRepository.existsByToken(oldRefreshToken)) {
            return jwtUtil.reissueToken(oldRefreshToken);
        } else {
            throw new CustomException(AuthErrorCode.BAD_REQUEST_400);
        }
    }

    public JwtDTO login(MemberReqDTO.LoginRequestDTO loginRequestDTO) {

        String email = loginRequestDTO.email();

        CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(email);

        return new JwtDTO(jwtUtil.createJwtAccessToken(userDetails),
                jwtUtil.createJwtRefreshToken(userDetails));
    }


    //로그아웃
    public void logout(String email) {

        int deletedCount = tokenRepository.deleteByEmail(email);

        if (deletedCount == 0) {
            log.warn("[ Logout ]삭제할 토큰이 존재하지 않습니다.");
        } else {
            log.info("[ Logout ] 로그아웃이 완료되었습니다.");
        }
    }

    //닉네임 변경
    public void updateNickname(String email, MemberReqDTO.UpdateNicknameDTO updateNicknameDTO) {

        String newNickname = updateNicknameDTO.newNickname();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.getNickname().equals(newNickname)) {
            throw new CustomException(SAME_VALUE);
        }

        memberRepository.updateNicknameByEmail(email, newNickname);
    }

    //패스워드 변경
    public void updatePassword(String email,
                               MemberReqDTO.UpdatePasswordDTO updatePasswordDTO) {

        String newPassword = updatePasswordDTO.newPassword();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.getPassword().equals(newPassword)) {
            throw new CustomException(PASSWORD_UNCHANGED);
        }

        memberRepository.updatePasswordByEmail(email, newPassword);
    }

    public boolean checkEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

}
