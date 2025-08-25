package org.example.team2backend.domain.member.repository;

import org.example.team2backend.domain.member.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface  EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByEmailAndCode(String email, String code);

    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.email = :email")
    void deleteByEmail(String email);
}

