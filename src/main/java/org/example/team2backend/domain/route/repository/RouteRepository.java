package org.example.team2backend.domain.route.repository;

import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.entity.RoutePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {



}
