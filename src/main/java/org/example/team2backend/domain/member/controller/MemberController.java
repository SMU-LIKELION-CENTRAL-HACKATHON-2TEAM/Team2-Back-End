package org.example.team2backend.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.service.command.MemberCommandService;
import org.example.team2backend.domain.member.service.query.MemberQueryService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.example.team2backend.global.security.auth.CustomUserDetails;
import org.example.team2backend.global.security.jwt.JwtDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "Email Login API", description = "일반 로그인 관련 API by 한민")
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final BCryptPasswordEncoder passwordEncoder;

    //회원가입
    @Operation(summary = "회원가입", description = "회원가입 API 입니다.")
    @PostMapping("")
    public CustomResponse<?> createUser(@RequestBody MemberReqDTO.SignUpRequestDTO signUpRequestDTO) {

        memberCommandService.createUser(signUpRequestDTO, passwordEncoder);

        return CustomResponse.onSuccess("회원가입 완료");
    }

    //로그인
    @Operation(summary = "로그인", description = "로그인 API 입니다.")
    @PostMapping("/login")
    public CustomResponse<?> login(@RequestBody MemberReqDTO.LoginRequestDTO loginRequestDTO) {

        return CustomResponse.onSuccess(memberCommandService.login(loginRequestDTO));
    }

    //토큰 재발급
    @SecurityRequirement(name = "JWT TOKEN")
    @Operation(summary = "토큰 재발급", description = "토큰 재발급 API 입니다.")
    @PostMapping("/reissue")
    public CustomResponse<?> reissueToken(@RequestBody JwtDTO jwtDTO) throws SignatureException {

        log.info("[ Member Controller ] 토큰을 재발급 합니다.");

        return CustomResponse.onSuccess(memberCommandService.reissueToken(jwtDTO));
    }

    //사용자 정보 조회
    @SecurityRequirement(name = "JWT TOKEN")
    @Operation(summary = "사용자 기본 정보 조회", description = "사용자 기본 정보 조회 API 입니다.")
    @GetMapping("/me")
    public CustomResponse<?> showUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("[ Member Controller ] 사용자의 정보를 조회합니다.");

        String email = userDetails.getUsername();

        return CustomResponse.onSuccess(memberQueryService.showMemberInfo(email));
    }

    //로그아웃
    @SecurityRequirement(name = "JWT TOKEN")
    @Operation(summary = "로그아웃", description = "로그아웃 API 입니다.")
    @PostMapping("/me")
    public CustomResponse<?> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("[ Member Controller ] 로그아웃");

        String email = userDetails.getUsername();

        memberCommandService.logout(email);

        return CustomResponse.onSuccess("로그아웃 완료");
    }

    //회원 탈퇴
    @SecurityRequirement(name = "JWT TOKEN")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API 입니다.")
    @DeleteMapping("/me")
    public CustomResponse<?> deleteMember(@AuthenticationPrincipal UserDetails userDetails) {

        log.info("[ Member Controller ] 회원 탈퇴");

        String email = userDetails.getUsername();

        memberCommandService.delete(email);

        return CustomResponse.onSuccess("회원 탈퇴 완료");

    }

    //닉네임 변경
    @SecurityRequirement(name = "JWT TOKEN")
    @Operation(summary = "사용자 닉네임 변경", description = "사용자 닉네임 변경 API 입니다.")
    @PatchMapping("/me/nickname")
    public CustomResponse<?> updateNickname(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody MemberReqDTO.UpdateNicknameDTO updateNicknameDTO) {

        log.info("[ Member Controller ] 사용자 닉네임 변경");

        String email = userDetails.getUsername();

        memberCommandService.updateNickname(email, updateNicknameDTO);

        return CustomResponse.onSuccess("닉네임 변경 완료");
    }

    //비밀번호 변경
    @SecurityRequirement(name = "JWT TOKEN")
    @Operation(summary = "사용자 비밀번호 변경", description = "사용자 비밀번호 변경 API 입니다.")
    @PatchMapping("/me/password")
    public CustomResponse<?> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody MemberReqDTO.UpdatePasswordDTO updatePasswordDTO) {

        log.info("[ Member Controller ] 사용자 패스워드 변경");

        String email = userDetails.getUsername();

        memberCommandService.updatePassword(email, updatePasswordDTO);

        return CustomResponse.onSuccess("패스워드 수정 완료");
    }

    //이메일 중복 검증
    @Operation(summary = "이메일 중복 검증", description = "이메일 중복 검증 API 입니다.")
    @PostMapping("/email")
    public CustomResponse<String> checkEmail(@RequestBody MemberReqDTO.MailRequestDTO mailRequestDTO) {
        boolean isDuplicate = memberCommandService.checkEmail(mailRequestDTO.email());

        if (isDuplicate) {
            return CustomResponse.onSuccess("이미 사용 중인 이메일입니다.");
        } else {
            return CustomResponse.onSuccess("사용 가능한 이메일입니다.");
        }
    }
}
