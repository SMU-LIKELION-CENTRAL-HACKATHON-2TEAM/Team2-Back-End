package org.example.team2backend.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.dto.request.MemberRouteReqDTO;
import org.example.team2backend.domain.member.service.command.MemberRouteCommandService;
import org.example.team2backend.domain.member.service.query.MemberRouteQueryService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.example.team2backend.global.security.auth.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scraps")
@Tag(name = "Scrap API", description = "루트 스크랩 관련 API by 한민")
public class MemberRouteController {

    private final MemberRouteCommandService memberRouteCommandService;
    private final MemberRouteQueryService memberRouteQueryService;

    @Operation(summary = "사용자 스크랩 루트 전체 조회", description = "사용자가 스크랩한 루트 전체를 조회합니다.")
    @GetMapping("/me")

    public CustomResponse<?> getScrap(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @Parameter(description = "커서 (null 또는 0이면 첫 페이지)") @RequestParam(required = false) Long cursor,
                                      @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {

        String email = userDetails.getUsername();

        return CustomResponse.onSuccess(memberRouteQueryService.getScrapList(email, cursor, size));
    }

    @Operation(summary = "루트 스크랩 토글", description = "루트에 스크랩을 취소합니다.")
    @PostMapping("/me/{routeId}")
    public CustomResponse<?> updateScrap(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         MemberRouteReqDTO.ScrapRequestDTO scrapRequestDTO) {

        String email = userDetails.getUsername();

        Long routeId = scrapRequestDTO.routeId();

        memberRouteCommandService.toggleScrap(email, routeId);

        return CustomResponse.onSuccess("루트 스크랩 토글 완료");
    }
}

