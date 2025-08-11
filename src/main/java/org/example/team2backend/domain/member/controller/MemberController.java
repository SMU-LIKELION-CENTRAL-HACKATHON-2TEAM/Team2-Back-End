package org.example.team2backend.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.service.command.MemberCommandService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Email Login", description = "이메일 로그인 관련 API by 한민")
public class MemberController {

    private final MemberCommandService memberCommandService;

    @Operation(summary = "회원가입 API", description = "회원가입 API 입니다.")
    @PostMapping("/users")
    public CustomResponse<?> createUser(@RequestBody MemberReqDTO.LoginRequestDTO loginReqDTO) {

        return CustomResponse.onSuccess(memberCommandService.createUser(loginReqDTO));
    }

}
