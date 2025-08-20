package org.example.team2backend.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.dto.request.MemberRouteReqDTO;
import org.example.team2backend.domain.member.service.command.MemberRouteCommandService;
import org.example.team2backend.domain.member.service.query.MemberQueryService;
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

    private final MemberQueryService memberQueryService;
    private final MemberRouteCommandService memberRouteCommandService;

    @Operation(summary = "사용자 스크랩 루트 전체 조회", description = "사용자가 스크랩한 루트 전체를 조회합니다.")
    @GetMapping("/me")
    public CustomResponse<?> getScrap(@AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();

        return CustomResponse.onSuccess(memberQueryService.showMemberInfo(email));
    }

    @Operation(summary = "사용자 스크랩 루트 추가", description = "스크랩 루트를 추가합니다.")
    @PostMapping("/me")
    public CustomResponse<?> updateScrap(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         MemberRouteReqDTO.ScrapRequestDTO scrapRequestDTO) {

        String email = userDetails.getUsername();

        Long routeId = scrapRequestDTO.routeId();

        memberRouteCommandService.addScrap(email, routeId);


        return CustomResponse.onSuccess("스크랩 추가 완료");
    }

    @Operation(summary = "사용자 스크랩 루트 삭제", description = "스크랩 루트를 삭제합니다.")
    @DeleteMapping("/me")
    public CustomResponse<?> deleteScrap(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         MemberRouteReqDTO.ScrapRequestDTO scrapRequestDTO) {

        String email = userDetails.getUsername();

        Long routeId = scrapRequestDTO.routeId();


        return CustomResponse.onSuccess("스크랩 추가 완료");
    }

}

