package com.icandoit.boottalk.coffeeChat.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;
import com.icandoit.boottalk.libs.entity.BaseEntity;
import com.icandoit.boottalk.user.domain.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coffee_chat_info")
@Slf4j
public class CoffeeChatInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coffeeChatInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User mentor;

    @Column(nullable = false, updatable = false)
    private String mentorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorType mentorType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Column(nullable = false)
    private String introduction;

    @Column(nullable = false, columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private LocalDateTime mentoringBanEndDate;

    @Builder.Default
    @OneToMany(mappedBy = "coffeeChatInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoffeeChatTime> availableTimes = new ArrayList<>();

    public static CoffeeChatInfo of(User mentor, String mentorName, MentorType mentorType,
        JobType jobType,
        String introduction) {
        return CoffeeChatInfo.builder()
            .mentor(mentor)
            .mentorName(mentorName)
            .mentorType(mentorType)
            .jobType(jobType)
            .introduction(introduction)
            .mentoringBanEndDate(LocalDateTime.now())
                // 커피챗 리스트 조회 시, 멘토링 활동 금지 당한 커피챗 제외하기 위해 entoringBanEndDate 필드 사용
                // null 값은 쿼리 성능을 떨어뜨릴 수 있어, 기본값으로 현재 시간 설정하여 null을 회피
            .build();
    }

    public void update(CoffeeChatInfoRequestDto requestDto) {
        this.mentorType = requestDto.mentorType();
        this.jobType = requestDto.jobType();
        this.introduction = requestDto.introduction();
    }

    public void addAvailableTime(CoffeeChatTime time) {
        availableTimes.add(time);
        time.setCoffeeChatInfo(this);
    }

    // 멘토링 활동 금지 패널티 적용
    public void applyMentoringBan(int duration) {
        this.mentoringBanEndDate = LocalDateTime.now().plusDays(duration);
    }

    // 멘토리 활동 금지 여부 확인
    public boolean isMentoringBanned(){
        return this.mentoringBanEndDate.isAfter(LocalDateTime.now());
    }

}
