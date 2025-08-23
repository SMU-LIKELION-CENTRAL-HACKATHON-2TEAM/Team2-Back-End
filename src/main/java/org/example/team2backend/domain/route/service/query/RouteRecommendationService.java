package org.example.team2backend.domain.route.service.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.exception.MemberErrorCode;
import org.example.team2backend.domain.member.exception.MemberException;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.domain.member.repository.MemberRouteRepository;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.route.dto.response.RouteResDTO;
import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.entity.RoutePlace;
import org.example.team2backend.domain.route.repository.RoutePlaceRepository;
import org.example.team2backend.domain.route.repository.RouteRepository;
import org.example.team2backend.global.openai.OpenAiService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RouteRecommendationService {

    private final OpenAiService openAiService;
    private final RouteRepository routeRepository;
    private final RoutePlaceRepository routePlaceRepository;
    private final MemberRepository memberRepository;
    private final MemberRouteRepository memberRouteRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public List<RouteResDTO.RouteDTO> recommendRoutes(String address, String email) throws IOException {

        //DB에서 모든 루트 가져오기
        List<Route> candidates = routeRepository.findAll();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        //후보 루트를 JSON 형식으로 변환 (RouteDTO 구조 맞게)
        StringBuilder prompt = new StringBuilder();
        prompt.append("사용자가 현재 위치한 주소: ").append(address).append("\n\n")
                .append("DB에 저장된 루트 목록(RouteDTO JSON 형식):\n");

        for (Route r : candidates) {
            prompt.append(String.format(
                    "{ \"routeId\": %d, \"name\": \"%s\", \"visitCount\": %d, \"bookmarked\": %d, \"viewCount\": %d }\n",
                    r.getId(), r.getName(),
                    r.getVisitCount() != null ? r.getVisitCount() : 0,
                    r.getBookmarked() != null ? r.getBookmarked() : 0,
                    r.getViewCount() != null ? r.getViewCount() : 0
            ));
        }

        //AI에게 5개 추천하도록 요청
        prompt.append("\n이 중에서 추천 루트 5개를 선택해라.\n")
                .append("출력은 반드시 **배열(JSON array)** 형식으로만 반환하라.\n")
                .append("절대 객체로 감싸지 말고, 절대 다른 텍스트나 설명을 추가하지 마라.\n")
                .append("형식:\n")
                .append("[\n")
                .append("  { \"routeId\": 숫자, \"reason\": \"추천 이유\" },\n")
                .append("  ... 총 5개\n")
                .append("]");



        //OpenAI API 호출
        String aiResponse = openAiService.getChatCompletion(prompt.toString());


        //OpenAI 응답(JSON) 파싱
        JsonNode root = mapper.readTree(aiResponse);
        String content = root.get("choices").get(0).get("message").get("content").asText();

        //응답을 RouteDTO 배열로 변환
        List<RouteResDTO.SimpleRouteDTO> aiResults;
        if (content.startsWith("[")) {
            aiResults = Arrays.asList(mapper.readValue(content, RouteResDTO.SimpleRouteDTO[].class));
        } else if (content.startsWith("{")) {
            // wrapper 객체일 경우
            record RoutesWrapper(List<RouteResDTO.SimpleRouteDTO> routes) {}
            RoutesWrapper wrapper = mapper.readValue(content, RoutesWrapper.class);
            aiResults = wrapper.routes();
        } else {
            throw new IOException("Unexpected AI response format: " + content);
        }


        List<Long> routeIds = aiResults.stream()
                .map(RouteResDTO.SimpleRouteDTO::routeId)
                .toList();

        List<Route> recommendedRoutes = routeRepository.findAllById(routeIds);

        //선택된 루트들은 조회수 1 증가
        routeIds.forEach(routeRepository::increaseViewCount);

        return recommendedRoutes.stream()
                .map(route -> {
                    List<RoutePlace> routePlaces = routePlaceRepository.findByRoute(route);

                    List<RouteResDTO.PlaceDTO> places = routePlaces.stream()
                            .map(rp -> {
                                Place p = rp.getPlace();
                                return new RouteResDTO.PlaceDTO(
                                        p.getId(),
                                        p.getName(),
                                        p.getCategory(),
                                        p.getAddress(),
                                        p.getKakaoId(),
                                        p.getLat(),
                                        p.getLng(),
                                        p.getIsActive()
                                );
                            })
                            .toList();

                    //해당 루트의 스크랩 여부 확인
                    boolean isBookmarked = memberRouteRepository.existsByMemberAndRoute(member, route);

                    return new RouteResDTO.RouteDTO(
                            route.getId(),
                            route.getName(),
                            route.getSummary(),
                            route.getBookmarked(),
                            route.getViewCount(),
                            isBookmarked,
                            places.isEmpty() ? null : places.get(0), // startPlace
                            places.size() > 1 ? places.subList(1, places.size()) : List.of() // nextPlaces
                    );
                })
                .toList();
    }
}
