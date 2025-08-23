package org.example.team2backend.domain.route.dto.response;

import java.util.List;

public class RouteResDTO {

    //클라이언트에 넘겨줄 장소 정보
    public record PlaceDTO(
            Long placeId,
            String name,
            String category,
            String address,
            String kakaoId,
            Double lat,
            Double lng,
            Boolean isActivate
    ) {}

    //클라이언트에 넘겨줄 루트 정보
    public record RouteDTO(
            Long routeId,
            String name,
            String summary,
            Long bookmarked, //해당 루트의 북마크(스크랩 수)
            Long viewCount, //해당 루트의 조회수
            Boolean isBookMarked, //사용자의 해당 루트 북마크(스크랩) 여부
            PlaceDTO startPlace,
            List<PlaceDTO> places
    ) {}

    // 📌 커서 기반 응답 DTO
    public record CursorResDTO<T>(
            List<T> content,
            boolean hasNext,
            Long nextCursor
    ) {}

    //ai 추천 파싱용
    public record SimpleRouteDTO(
       Long routeId,
       String reason
    ) {}



}
