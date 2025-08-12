package org.example.team2backend.domain.member.repository;

import org.example.team2backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m WHERE m.email = :email")
    Optional<Member> findByEmail(@Param("email") String email);


}
