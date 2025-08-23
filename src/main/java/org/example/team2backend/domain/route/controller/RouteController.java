package org.example.team2backend.domain.route.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.place.dto.request.PlaceReqDTO;
import org.example.team2backend.domain.place.service.command.PlaceCommandService;
import org.example.team2backend.domain.route.dto.request.RouteReqDTO;
import org.example.team2backend.domain.route.dto.response.RouteResDTO;
import org.example.team2backend.domain.route.service.command.RouteCommandService;
import org.example.team2backend.domain.route.service.query.RouteRecommendationService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
@Tag(name = "Route API", description = "루트 관련 API by 한민")
public class RouteController {

    private final RouteCommandService routeCommandService;
    private final RouteRecommendationService recommendationService;
    private final PlaceCommandService placeCommandService;

    //루트 생성
    @Operation(summary = "루트 생성", description = "루트 생성 api 입니다.")
    @PostMapping("")
    public CustomResponse<?> createRoute(@RequestBody RouteReqDTO.CreateRouteDTO createRouteDTO,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        routeCommandService.createRoute(createRouteDTO, userDetails.getUsername());

        return CustomResponse.onSuccess("루트 생성 성공");
    }

    //루트 단건 조회
    /*@Operation(summary = "루트 단건 조회 api", description = "루트 단건 조회 api 입니다.")
    @GetMapping("{kakaoId}")
    public List<RouteResDTO.PlaceDTO> getPlaces(@PathVariable Long kakaoId) {

        return routeQueryService.getPlacesByRouteId(kakaoId)
                .stream()
                .map(RouteConverter::fromEntity)
                .toList();
    }*/

    //루트 추천
    @Operation(summary = "루트 추천", description = "open ai api를 이용하여 거리를 기반으로, 다음 방문 루트를 추천합니다.")
    @GetMapping("/recommend")
    public CustomResponse<?> recommendPlaces(
            @RequestBody PlaceReqDTO.UpdateReqDTO updateReqDTO, @AuthenticationPrincipal  UserDetails userDetails) throws IOException {

        String address = updateReqDTO.address();

        String email = userDetails.getUsername();

        return CustomResponse.onSuccess(recommendationService.recommendRoutes(address, email));
    }


}
