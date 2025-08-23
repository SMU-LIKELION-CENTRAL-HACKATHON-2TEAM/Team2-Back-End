package org.example.team2backend.domain.place.converter;

import org.example.team2backend.domain.place.dto.request.PlaceReqDTO;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.place.entity.PlaceImage;

public class PlaceConverter {

    //카카오아이디를 포함한 모든 필드를 초기화히여 엔티티로 변환
    public static Place toPlace(PlaceReqDTO.UpdateReqDTO updateReqDTO) {
        return Place.builder()
                .name(updateReqDTO.name())
                .category(updateReqDTO.category())
                .address(updateReqDTO.address())
                .kakaoId(updateReqDTO.kakaoId())
                .lat(updateReqDTO.lat())
                .lng(updateReqDTO.lng())
                .isActive(updateReqDTO.isActive())
                .build();

    }

    //장소 필드 수정(DTO값 덮어쓰기)
    public static void updatePlace(Place place, PlaceReqDTO.UpdateReqDTO updateReqDTO) {
        place.setName(updateReqDTO.name());
        place.setCategory(updateReqDTO.category());
        place.setAddress(updateReqDTO.address());
        place.setKakaoId(updateReqDTO.kakaoId());
        place.setLat(updateReqDTO.lat());
        place.setLng(updateReqDTO.lng());
        place.setIsActive(updateReqDTO.isActive());
    }

    public static PlaceImage toPlaceImage(String imageKey, String imageUrl, Place place) {
        return PlaceImage.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .place(place)
                .build();
    }
}
