package org.example.team2backend.domain.route.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.route.converter.RouteConverter;
import org.example.team2backend.domain.route.dto.request.RouteReqDTO;
import org.example.team2backend.domain.route.dto.response.RouteResDTO;
import org.example.team2backend.domain.route.service.command.RouteCommandService;
import org.example.team2backend.domain.route.service.query.RouteQueryService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
@Tag(name = "Route API", description = "루트 관련 API by 한민")
public class RouteController {

    private final RouteCommandService routeCommandService;
    private final RouteQueryService routeQueryService;

    //루트 생성
    @Operation(summary = "루트 생성 api", description = "루트 생성 api 입니다.")
    @PostMapping("")
    public CustomResponse<?> createRoute(@RequestBody RouteReqDTO.CreateRouteDTO createRouteDTO) {

        routeCommandService.createRoute(createRouteDTO);

        return CustomResponse.onSuccess("루트 생성 성공");
    }

    //루트 단건 조회
    @Operation(summary = "루트 단건 조회 api", description = "루트 단건 조회 api 입니다.")
    @GetMapping("{routeId}")
    public List<RouteResDTO.PlaceDTO> getPlaces(@PathVariable Long routeId) {

        return routeQueryService.getPlacesByRouteId(routeId)
                .stream()
                .map(RouteConverter::fromEntity) // 정적 메서드 사용
                .toList();
    }

}
