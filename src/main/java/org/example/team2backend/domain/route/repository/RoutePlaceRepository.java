package org.example.team2backend.domain.route.repository;

import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.entity.RoutePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutePlaceRepository extends JpaRepository<RoutePlace, Long> {

    //카카오 아이디를 이용해서 해당 방문 순서를 가진 매핑 테이블 조회
    @Query("SELECT rp FROM RoutePlace rp WHERE rp.visitOrder = :visitedOrder")
    List<RoutePlace> findByVisitOrder(@Param("visitOrder") Long visitOrder);

    List<RoutePlace> findByRoute(Route route);

    @Query("SELECT rp FROM RoutePlace rp JOIN FETCH rp.place WHERE rp.route.id = :routeId ORDER BY rp.visitOrder ASC")
    List<RoutePlace> findAllByRouteId(@Param("routeId") Long routeId);
}
