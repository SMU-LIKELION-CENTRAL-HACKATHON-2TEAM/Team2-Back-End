package org.example.team2backend.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.team2backend.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "code")
    private String code;

    @Column(name = "verified")
    private boolean verified;

    @Column(nullable = false)
    private LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(10);

    public void verify(){
        this.verified = true;
    }

    //민료 여부 반환
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
