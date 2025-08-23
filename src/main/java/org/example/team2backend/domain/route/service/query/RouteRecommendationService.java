package org.example.team2backend.domain.route.service.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.place.repository.PlaceRepository;
import org.example.team2backend.domain.route.dto.response.RouteResDTO;
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
    private final PlaceRepository placeRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public List<RouteResDTO.RouteDTO> recommendRoutes(String address, double lat, double lng) throws IOException {
        // 1. DB에서 주변 장소 가져오기
        List<Place> candidates = placeRepository.findNearby(lat, lng);

        // 2. 후보 장소를 PlaceDTO 형식으로 JSON 변환
        StringBuilder prompt = new StringBuilder();
        prompt.append("사용자가 현재 위치한 주소: ").append(address).append("\n\n")
                .append("DB에 저장된 주변 장소 목록(PlaceDTO JSON 형식):\n");

        for (Place p : candidates) {
            prompt.append("{")
                    .append("\"placeId\": ").append(p.getId()).append(", ")
                    .append("\"placeName\": \"").append(p.getName()).append("\", ")
                    .append("\"category\": \"").append(p.getCategory()).append("\", ")
                    .append("\"address\": \"").append(p.getAddress()).append("\", ")
                    .append("\"kakaoId\": \"").append(p.getKakaoId()).append("\", ")
                    .append("\"lat\": ").append(p.getLat()).append(", ")
                    .append("\"lng\": ").append(p.getLng()).append(", ")
                    .append("\"isActivate\": ").append(p.getIsActive())
                    .append("}\n");
        }

        prompt.append("\n위 장소들을 활용해 루트를 5개 추천해줘. ")
                .append("각 루트는 JSON 객체로 구성하고, 구조는 반드시 다음과 같아야 한다:\n")
                .append("[\n")
                .append("  {\n")
                .append("    \"startPlace\": PlaceDTO,\n")
                .append("    \"nextPlaces\": [PlaceDTO, PlaceDTO],\n")
                .append("    \"description\": \"한 줄 설명\"\n")
                .append("  }, ... 5개 ]\n");

        // 3. OpenAI API 호출
        String aiResponse = openAiService.getChatCompletion(prompt.toString());

        // 4. OpenAI 응답(JSON) 파싱
        JsonNode root = mapper.readTree(aiResponse);
        String content = root.get("choices").get(0).get("message").get("content").asText();

        // 5. 응답을 RouteDTO 배열로 변환
        return Arrays.asList(mapper.readValue(content, RouteResDTO.RouteDTO[].class));
    }

}
