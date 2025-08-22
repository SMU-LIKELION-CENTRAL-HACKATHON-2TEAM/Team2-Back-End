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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlaceCommandService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;
    private final S3Service s3Service;

    //주소 저장
    public void updatePlace(PlaceReqDTO.UpdateReqDTO updateReqDTO) {

        //전달 받은 주소의 kakaoId
        String kakaoId = updateReqDTO.kakaoId();

        Optional<Place> opt = placeRepository.findByKakaoId(kakaoId);

        //해당 카카오 아이디가 존재한다면
        if (opt.isPresent()) {
            //opt의 내용을 place에 옮겨 담은 뒤, 수정
            Place place = opt.get(); //opt가 존재한다는 것이 보장 되었으므로 문제 없을 듯 합니다.
            PlaceConverter.updatePlace(place, updateReqDTO);
            //해당 카카오 아이디가 존재하지 않는다면
        } else {
            //엔티티로 변환한 후, 저장
            Place place = PlaceConverter.toPlace(updateReqDTO);
            placeRepository.save(place);
        }

        log.info("[ PlaceCommandController ] 장소 업데이트 완료");
    }

    private void validateImageCount(List<MultipartFile> images) {
        if (images.size() > 1) {
            throw new PlaceException(PlaceErrorCode.PLACE_IMAGE_LIMIT);
        }
    }

    private void savePlaceImage(MultipartFile image, Place place) {
        String imageKey = s3Service.upload(image, "place");
        String imageUrl = s3Service.getFileUrl(imageKey);

        PlaceImage placeImage = PlaceConverter.toPlaceImage(imageKey, imageUrl, place);
        placeImageRepository.save(placeImage);
    }
}
