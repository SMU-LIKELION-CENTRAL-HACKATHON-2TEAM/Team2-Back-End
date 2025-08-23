package org.example.team2backend.domain.place.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.place.converter.PlaceConverter;
import org.example.team2backend.domain.place.dto.request.PlaceReqDTO;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.place.entity.PlaceImage;
import org.example.team2backend.domain.place.exception.PlaceErrorCode;
import org.example.team2backend.domain.place.exception.PlaceException;
import org.example.team2backend.domain.place.repository.PlaceImageRepository;
import org.example.team2backend.domain.place.repository.PlaceRepository;
import org.example.team2backend.global.s3.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlaceCommandService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;
    private final S3Service s3Service;

    //주소 저장
    public void updatePlace(PlaceReqDTO.UpdateReqDTO updateReqDTO, List<MultipartFile> images) {

        //전달 받은 주소의 식별값
        String kakaoId = updateReqDTO.kakaoId();

        //장소가 있으면 업데이트 후 장소 반환
        Place place = placeRepository.findByKakaoId(kakaoId)
                .map(existing -> {
                    PlaceConverter.updatePlace(existing, updateReqDTO);
                    return existing;
                })
                //없으면 새 장소 만들고 장소 반환
                .orElseGet(() -> {
                    Place newPlace = PlaceConverter.toPlace(updateReqDTO);
                    return placeRepository.save(newPlace);
                });


        //이미지 갯수 검증 후 등록
        validateImageCount(images);

        if (CollectionUtils.isEmpty(images)) {
            placeImageRepository.deleteByPlace(place);
        } else {
            placeImageRepository.deleteByPlace(place);
            images.forEach(image -> savePlaceImage(image, place));
        }

        log.info("[ PlaceCommandController ] 장소 업데이트 완료");
    }

    private void validateImageCount(List<MultipartFile> images) {
        if (images != null && images.size() > 1) {
            throw new PlaceException(PlaceErrorCode.PLACE_IMAGE_LIMIT);
        }
    }

    private void savePlaceImage(MultipartFile image, Place place) {
        String imageKey = null;
        try {
            imageKey = s3Service.upload(image, "place");
            String imageUrl = s3Service.getFileUrl(imageKey);

            PlaceImage placeImage = PlaceConverter.toPlaceImage(imageKey, imageUrl, place);
            placeImageRepository.save(placeImage);
        } catch (Exception e){
            // DB 저장 중 실패했으면 S3에서 삭제
            if (imageKey != null) {
                s3Service.deleteFile(imageKey);
            }
            throw e; // 예외 다시 던져서 롤백
        }

    }
}
