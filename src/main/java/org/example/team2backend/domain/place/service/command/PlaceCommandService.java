package org.example.team2backend.domain.place.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.place.converter.PlaceConverter;
import org.example.team2backend.domain.place.dto.request.PlaceReqDTO;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.place.exception.PlaceErrorCode;
import org.example.team2backend.domain.place.exception.PlaceException;
import org.example.team2backend.domain.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlaceCommandService {

    private final PlaceRepository placeRepository;

    //주소 저장
    public void updatePlace(PlaceReqDTO.UpdateReqDTO updateReqDTO) {

        //전달 받은 주소의 식별값
        String kakaoId = updateReqDTO.kakaoId();

        //식별값으로 장소 조회
        Optional<Place> opt = placeRepository.findByKakaoId(kakaoId);

        //장소가 db에 있다면
        if (opt.isPresent()) {
            //opt의 내용을 place에 옮겨 담은 뒤
            Place place = opt.get();
            //덮어쓰기
            PlaceConverter.updatePlace(place, updateReqDTO);
        //장소가 db에 없다면
        } else {
            //전달 받은 내용을 엔티티로 변환한 후, 저장
            Place place = PlaceConverter.toPlace(updateReqDTO);
            placeRepository.save(place);
        }

        log.info("[ PlaceCommandController ] 장소 업데이트 완료");
    }
}
