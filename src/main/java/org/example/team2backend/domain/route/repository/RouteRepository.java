package org.example.team2backend.domain.route.repository;

import org.example.team2backend.domain.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RouteRepository extends JpaRepository<Route, Long> {
}
