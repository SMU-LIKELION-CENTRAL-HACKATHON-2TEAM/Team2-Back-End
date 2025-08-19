package org.example.team2backend.domain.place.converter;

import org.example.team2backend.domain.place.dto.request.PlaceReqDTO;
import org.example.team2backend.domain.place.entity.Place;

public class PlaceConverter {

    public static Place toPlace(PlaceReqDTO.UpdateReqDTO updateReqDTO) {
        return Place.builder()
                .name(updateReqDTO.placeName())
                .category(updateReqDTO.category())
                .address(updateReqDTO.address())
                .lat(updateReqDTO.lat())
                .lng(updateReqDTO.lng())
                .isActive(updateReqDTO.isActivate())
                .description(updateReqDTO.description())
                .build();
    }

    //엔티티를 새로 만들지 않고, 변경이 들어간 값이 있는 필드만 수정
    //솔직히 비효율적인 것 같긴 한데, 이게 제가 생각할 수 있는 최선이었습니다...
    public static void updatePlace(Place place, PlaceReqDTO.UpdateReqDTO updateReqDTO) {
        if (!updateReqDTO.placeName().equals(place.getName()))
            place.setName(updateReqDTO.placeName());
        if (!updateReqDTO.category().equals(place.getCategory()))
            place.setCategory(updateReqDTO.category());
        if (!updateReqDTO.address().equals(place.getAddress()))
            place.setAddress(updateReqDTO.address());
        if (!updateReqDTO.lat().equals(place.getLat()))
            place.setLat(updateReqDTO.lat());
        if (!updateReqDTO.lng().equals(place.getLng()))
            place.setLng(updateReqDTO.lng());
        if (!updateReqDTO.description().equals(place.getDescription()))
            place.setDescription(updateReqDTO.description());
        if (!updateReqDTO.isActivate().equals(place.getIsActive()))
            place.setIsActive(updateReqDTO.isActivate());
    }

    public static Place toPlaceWithKakao(PlaceReqDTO.UpdateReqDTO updateReqDTO) {
        return Place.builder()
                .name(updateReqDTO.placeName())
                .category(updateReqDTO.category())
                .address(updateReqDTO.address())
                .kakaoId(updateReqDTO.kakaoId())
                .lat(updateReqDTO.lat())
                .lng(updateReqDTO.lng())
                .isActive(updateReqDTO.isActivate())
                .description(updateReqDTO.description())
                .build();

    }
}
