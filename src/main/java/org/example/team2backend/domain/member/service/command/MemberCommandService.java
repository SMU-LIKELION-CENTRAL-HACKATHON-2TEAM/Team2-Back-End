    package org.example.team2backend.domain.member.service.command;

    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.example.team2backend.domain.member.converter.MemberConverter;
    import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
    import org.example.team2backend.domain.member.entity.Member;
    import org.example.team2backend.domain.member.exception.MemberErrorCode;
    import org.example.team2backend.domain.member.exception.MemberException;
    import org.example.team2backend.domain.member.repository.MemberRepository;
    import org.example.team2backend.domain.member.repository.TokenRepository;
    import org.example.team2backend.global.apiPayload.code.AuthErrorCode;
    import org.example.team2backend.global.apiPayload.exception.CustomException;
    import org.example.team2backend.global.security.auth.AuthException;
    import org.example.team2backend.global.security.auth.CustomUserDetails;
    import org.example.team2backend.global.security.jwt.JwtDTO;
    import org.example.team2backend.global.security.jwt.JwtUtil;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.BadCredentialsException;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.security.SignatureException;

    import static org.example.team2backend.domain.member.exception.MemberErrorCode.*;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    @Transactional
    public class MemberCommandService {

        private final MemberRepository memberRepository;
        private final JwtUtil jwtUtil;
        private final TokenRepository tokenRepository;
        private final AuthenticationManager authenticationManager;

        //회원 가입(생성)
        public void createUser(MemberReqDTO.SignUpRequestDTO signUpRequestDTO, PasswordEncoder passwordEncoder) {
            //멤버 생성
            Member member = MemberConverter.toMember(signUpRequestDTO, passwordEncoder);
            //입력한 패스워드가 확인용 패스워드와 다르다면 예외 발생
            if (!signUpRequestDTO.password().equals(signUpRequestDTO.confirmPassword())) {
                throw new MemberException(MemberErrorCode.PASSWORD_MISMATCH);
            }

            if (memberRepository.findByEmail(signUpRequestDTO.email()).isPresent()) {
                Member existing = memberRepository.findByEmail(signUpRequestDTO.email()).get();
                if (existing.getIsDeleted()) {
                    //사용자 복원
                    existing.restore(signUpRequestDTO, passwordEncoder);
                    log.info("[ CreateUser ] 회원이 복구되었습니다.");
                } else {
                    throw new AuthException(AuthErrorCode.BAD_REQUEST_400);
                }
            } else {
                memberRepository.save(member);
            }
        }

        //refreshToken을 바탕으로 토큰 재발급
        public JwtDTO reissueToken(JwtDTO jwtDTO) throws SignatureException {

            String oldRefreshToken = jwtDTO.jwtRefreshToken();

            Member member = memberRepository.findByEmail(jwtUtil.getEmail(oldRefreshToken))
                    .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

            //삭제된 회원일 경우
            if (member.getIsDeleted()) {
                throw new MemberException(MemberErrorCode.MEMBER_DELETED);
            }

            if (!tokenRepository.existsByToken(oldRefreshToken)) {
                return jwtUtil.reissueToken(oldRefreshToken);
            } else {
                throw new CustomException(AuthErrorCode.BAD_REQUEST_400);
            }
        }

        //로그인
        public JwtDTO login(MemberReqDTO.LoginRequestDTO loginRequestDTO) {

            //email + password 기반 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.email(),
                            loginRequestDTO.password()
                    );

            try {
                // 실제 인증 (UserDetailsService + PasswordEncoder 자동 적용)
                Authentication authentication = authenticationManager.authenticate(authenticationToken);

                // 인증 성공 시 CustomUserDetails 반환
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                Member member = memberRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

                //멤버 삭제 여부 검사
                if (member.getIsDeleted()){
                    throw new MemberException(MemberErrorCode.MEMBER_DELETED);
                }

                return new JwtDTO(jwtUtil.createJwtAccessToken(userDetails),
                        jwtUtil.createJwtRefreshToken(userDetails));
            //이메일이 잘못 되었을 경우
            } catch (UsernameNotFoundException e) {
                throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
            //패스워드가 잘못 되었을 경우
            } catch (BadCredentialsException e){
                throw new MemberException(MemberErrorCode.INVALID_CREDENTIALS);
            }
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

        //회원 탈퇴
        public void delete(String email) {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

            member.softDelete();

            log.info("[ Delete ] 회원 탈퇴가 완료되었습니다.");
        }

        //닉네임 변경
        public void updateNickname(String email, MemberReqDTO.UpdateNicknameDTO updateNicknameDTO) {

            String newNickname = updateNicknameDTO.newNickname();

            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

            //회원 삭제 검사
            if (member.getIsDeleted()) {
                throw new MemberException(MemberErrorCode.MEMBER_DELETED);
            }

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

            //회원 삭제 검사
            if (member.getIsDeleted()) {
                throw new MemberException(MemberErrorCode.MEMBER_DELETED);
            }

            if (member.getPassword().equals(newPassword)) {
                throw new CustomException(PASSWORD_UNCHANGED);
            }

            memberRepository.updatePasswordByEmail(email, newPassword);
        }

        public boolean checkEmail(String email) {
            return memberRepository.findByEmail(email).isPresent();
        }

    }
