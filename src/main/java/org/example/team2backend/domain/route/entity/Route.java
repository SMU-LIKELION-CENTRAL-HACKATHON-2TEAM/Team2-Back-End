package org.example.team2backend.domain.route.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.global.entity.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "route")
public class Route extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    //해당 루트에 대한 AI 요약
    @Column(name = "summary")
    private String summary;

    @Column(name = "visit_count")
    private Long visitCount;

    @Column(name = "save_count")
    private Long saveCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;
}
