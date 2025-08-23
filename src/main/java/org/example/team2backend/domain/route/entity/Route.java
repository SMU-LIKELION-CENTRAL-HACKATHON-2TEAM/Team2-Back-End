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

    //방문자 수
    @Column(name = "visit_count")
    private Long visitCount;

    //북마크(스크랩) 수
    @Column(name = "bookmarked")
    private Long bookmarked = 0L;

    //조회 수
    @Column(name = "view_count")
    private Long viewCount = 0L;

    //사용자의 북마크 여부
    @Column(name = "is_bookmarked")
    private Boolean isBookmarked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    public void linkMember(Member member){
        this.member = member;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
