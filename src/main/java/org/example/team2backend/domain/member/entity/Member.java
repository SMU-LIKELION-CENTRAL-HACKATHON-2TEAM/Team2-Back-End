package org.example.team2backend.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.global.entity.BaseEntity;
import org.example.team2backend.global.security.auth.Roles;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "role")
    private Roles role;

    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    public void softDelete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void restore(MemberReqDTO.SignUpRequestDTO signUpRequestDTO, PasswordEncoder passwordEncoder) {
        this.isDeleted = false;
        this.updatedAt = LocalDateTime.now();
        this.nickname = signUpRequestDTO.nickname();
        this.password = passwordEncoder.encode(signUpRequestDTO.password());
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRoute> memberRoutes = new ArrayList<>();
}
