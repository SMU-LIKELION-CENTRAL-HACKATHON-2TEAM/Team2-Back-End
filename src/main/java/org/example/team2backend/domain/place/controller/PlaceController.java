package org.example.team2backend.domain.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.place.dto.request.PlaceReqDTO;
import org.example.team2backend.domain.place.service.command.PlaceCommandService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places")
@Tag(name = "Place API", description = "장소 관련 API by 한민")
public class PlaceController {

    private final PlaceCommandService placeCommandService;

    //장소 등록
    @Operation(summary = "장소 등록",
            description = "멀티파트 요청으로 장소 JSON + 이미지 파일을 함께 업로드합니다.")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomResponse<?> updatePlace(
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart("place") PlaceReqDTO.UpdateReqDTO updateReqDTO
            ) {

        placeCommandService.updatePlace(updateReqDTO, images);

        return CustomResponse.onSuccess("장소 등록(업데이트) 완료");
    }

}
