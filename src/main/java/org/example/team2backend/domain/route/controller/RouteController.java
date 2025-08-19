package org.example.team2backend.domain.route.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.route.dto.request.RouteReqDTO;
import org.example.team2backend.domain.route.service.command.RouteCommandService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
@Tag(name = "Route API", description = "루트 관련 API by 한민")
public class RouteController {

    private final RouteCommandService routeCommandService;

    //루트 생성
    @Operation(summary = "루트 생성 api", description = "루트 생성 api 입니다.")
    @PostMapping("")
    public CustomResponse<?> createRoute(@RequestBody RouteReqDTO.CreateRouteDTO createRouteDTO) {

        routeCommandService.createRoute(createRouteDTO);

        return CustomResponse.onSuccess("루트 생성 성공");
    }

}
