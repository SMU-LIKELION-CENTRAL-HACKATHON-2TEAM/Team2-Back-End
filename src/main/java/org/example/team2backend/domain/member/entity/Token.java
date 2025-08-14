package org.example.team2backend.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "token")
public class Token {

    @Id
    private String email;

    private String token;

    @OneToOne
    @Setter
    @JoinColumn(name = "member_id")
    private Member member;
}
