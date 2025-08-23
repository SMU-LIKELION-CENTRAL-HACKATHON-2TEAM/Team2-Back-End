package org.example.team2backend.domain.route.repository;

import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.entity.RoutePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutePlaceRepository extends JpaRepository<RoutePlace, Long> {

    List<RoutePlace> findByRoute(Route route);

    @Query("SELECT rp FROM RoutePlace rp JOIN FETCH rp.place WHERE rp.route.id = :routeId ORDER BY rp.visitOrder ASC")
    List<RoutePlace> findAllByKakaoId(@Param("kakaoId") Long kakaoId);

    @Query("SELECT rp FROM RoutePlace rp " +
            "JOIN FETCH rp.place " +
            "WHERE rp.route.id IN :routeIds AND rp.visitOrder = :visitOrder")
    List<RoutePlace> findByRouteIdInAndVisitOrder(
            @Param("routeIds") List<Long> routeIds,
            @Param("visitOrder") Integer visitOrder
    );

    List<RoutePlace> findByPlace_KakaoIdAndVisitOrder(String kakaoId, int visitOrder);

}
