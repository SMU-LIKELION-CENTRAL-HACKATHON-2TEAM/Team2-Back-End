package org.example.team2backend.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.service.command.MailCommandService;
import org.example.team2backend.domain.member.service.command.MemberCommandService;
import org.example.team2backend.domain.member.service.query.MemberQueryService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/emails")
@Tag(name = "Email Verification", description = "이메일 인증 관련 API by 한민")
public class MailController {

    private final MailCommandService mailCommandService;

    private final MemberQueryService memberQueryService;

    @Operation(summary = "인증 코드 발급 API", description = "인증 코드 발급 API 입니다.")
    @PostMapping("")
    public CustomResponse<?> sendCode(@RequestBody MemberReqDTO.MailRequestDTO mailRequestDTO)
            throws MessagingException {

        String code = mailCommandService.sendSimpleMessage(mailRequestDTO.email());

        return CustomResponse.onSuccess(code);
    }

    @Operation(summary = "인증 코드 검증 API", description = "인증 코드 검증 API 입니다.")
    @PostMapping("/codes")
    public CustomResponse<?> verifyCode(@RequestBody MemberReqDTO.VerifyRequestDTO verifyRequestDTO) {

        memberQueryService.verifyCode(verifyRequestDTO);

        return CustomResponse.onSuccess("이메일 인증 완료");
    }

}
