package org.example.team2backend.domain.member.repository;

import org.example.team2backend.domain.member.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
