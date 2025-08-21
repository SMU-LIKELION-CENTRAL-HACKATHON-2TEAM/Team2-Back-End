package org.example.team2backend.domain.place.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.team2backend.global.entity.BaseEntity;

@Entity
@Getter
@Setter //모든 필드에 대해 setter가 필요하기 때문
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "place", uniqueConstraints = @UniqueConstraint(columnNames = {"id", "kakao_id"}))
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    // 정해지면 enum으로 수정
    @Column(name = "category")
    private String category;

    @Column(name = "address")
    private String address;

    //카카오 api의 장소 고유 식별값
    @Column(name = "kakao_id", nullable = false)
    private String kakaoId;

    //위도
    @Column(name = "lat")
    private Double lat;

    //경도
    @Column(name = "lng")
    private Double lng;

    @Column(name = "is_active")
    private Boolean isActive;

}
