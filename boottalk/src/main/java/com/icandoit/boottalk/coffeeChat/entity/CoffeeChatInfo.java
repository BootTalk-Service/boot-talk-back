package com.icandoit.boottalk.coffeeChat.entity;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.UserType;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coffee_chat_info")
public class CoffeeChatInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coffeeChatInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @Column(name = "introduction", nullable = false)
    private String introduction;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    public static CoffeeChatInfo of(User user, String usertype, String jobtype, String introduction) {
        return CoffeeChatInfo.builder()
            .user(user)
            .userType(UserType.valueOf(usertype))
            .jobType(JobType.valueOf(jobtype))
            .introduction(introduction)
            .build();
    }

    public void update(CoffeeChatInfoRequestDto requestDto) {
        this.userType = UserType.valueOf(requestDto.getUserType());
        this.jobType = JobType.valueOf(requestDto.getJobType());
        this.introduction = requestDto.getIntroduction();
    }

    public void deleted() {
        this.deleted = Boolean.TRUE;
    }
    public boolean isDeleted() {
        return this.deleted;
    }
}
