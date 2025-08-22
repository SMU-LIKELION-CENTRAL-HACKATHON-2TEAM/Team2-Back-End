package org.example.team2backend.domain.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.place.dto.request.PlaceReqDTO;
import org.example.team2backend.domain.place.service.command.PlaceCommandService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places")
@Tag(name = "Place API", description = "장소 관련 API by 한민")
public class PlaceController {

    private final PlaceCommandService placeCommandService;

    //장소 등록
    @Operation(summary = "장소 등록", description = "전달 받은 장소 정보를 DB에 저장(또는 수정)합니다.")
    @PostMapping("")
    public CustomResponse<?> updatePlace(
            @RequestBody PlaceReqDTO.UpdateReqDTO updateReqDTO) {

        placeCommandService.updatePlace(updateReqDTO);

        return CustomResponse.onSuccess("장소 등록(업데이트) 완료");
    }

}
