package org.example.team2backend.domain.member.repository;

import org.example.team2backend.domain.member.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT m FROM Token m WHERE m.email = :email")
    Optional<Token> findByEmail(@Param("email") String email);

    @Modifying
    @Query("DELETE FROM Token m WHERE m.email = :email")
    int deleteByEmail(@Param("email") String email);
}
