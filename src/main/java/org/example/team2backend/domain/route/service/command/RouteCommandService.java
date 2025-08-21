package org.example.team2backend.domain.route.service.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.place.repository.PlaceRepository;
import org.example.team2backend.domain.route.converter.RouteConverter;
import org.example.team2backend.domain.route.dto.request.RouteReqDTO;
import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.entity.RoutePlace;
import org.example.team2backend.domain.route.exception.RouteErrorCode;
import org.example.team2backend.domain.route.exception.RouteException;
import org.example.team2backend.domain.route.repository.RoutePlaceRepository;
import org.example.team2backend.domain.route.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static org.example.team2backend.domain.route.converter.RouteConverter.toRoute;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RouteCommandService {

    private final RouteRepository routeRepository;
    private final RoutePlaceRepository routePlaceRepository;
    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;

    //루트 생성
    public void createRoute(RouteReqDTO.CreateRouteDTO createRouteDTO, String email) {

        //멤버 객체 생성
        /*Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));*/
        
        //dto에서 리스트 형식으로 선택한 장소들 가져오기
        List<RouteReqDTO.PlaceDTO> newPlaces = createRouteDTO.places();

        // 모든 기존 루트 조회후 가져오기
        List<Route> existingRoutes = routeRepository.findAll();

        //각 루트 마다 비교
        for (Route existingRoute : existingRoutes) {
            //루트들로부터 모든 매핑 테이블 조회 및 정렬
            List<RoutePlace> existingPlaces = routePlaceRepository.findByRoute(existingRoute)
                    .stream()
                    .sorted(Comparator.comparing(RoutePlace::getVisitOrder))
                    .toList();


            boolean isSame = true;

            //루트 안에 속한 장소의 길이와 선택한 장소의 갯수가 다르면 아래 과정 스킵
            //길이가 다르면 무조건 다른 루트이기 때문
            if (existingPlaces.size() != newPlaces.size()) {
                continue;
            }

            //선택한 루트와 기존의 루트를 비교
            for (int i = 0; i < newPlaces.size(); i++) {
                RouteReqDTO.PlaceDTO newPlace = newPlaces.get(i);
                RoutePlace existing = existingPlaces.get(i);

                //두 장소의 카카오 아이디를 비교한 후, 두 아이디가 다르면 종료
                if (!existing.getPlace().getKakaoId().equals(newPlace.kakaoId())) {
                    isSame = false;
                    break;
                }
            }
            //두 루트가 같다면 예외 발생
            if (isSame) {
                log.warn("[ RouteCommandService ] 두 루트가 동일합니다.");
                throw new RouteException(RouteErrorCode.ROUTE_ALREADY_EXISTS);
            }
        }

        //루트 만들고 저장
        Route route = toRoute(createRouteDTO);
        //route.linkMember(member);
        routeRepository.save(route);
        log.info("[ RouteCommandService ] 루트 생성 후 저장.");

        for (int i = 0; i < newPlaces.size(); i++) {
            RouteReqDTO.PlaceDTO placeDTO = newPlaces.get(i);

            //카카오 아이디로 Place 조회 → 없으면 새로 저장
            log.info("[ RouteCommandService ] kakaoId로 장소를 조회합니다.");
            Place place = placeRepository.findByKakaoId(placeDTO.kakaoId())
                    .orElseGet(() -> placeRepository.save(RouteConverter.toPlaceWithKakao(placeDTO)));

            //매핑 테이블(RoutePlace) 생성
            //dto를 변환하는 것이 아니기 때문에 Converter는 사용하지 않았음.
            RoutePlace rp = RoutePlace.builder()
                    .route(route)
                    .place(place)
                    .visitOrder(i + 1)
                    .build();

            //매핑 테이블 저장
            routePlaceRepository.save(rp);
        }
        log.info("[ RouteCommandService ] 매핑 테이블 저장 완료.");
    }
}
