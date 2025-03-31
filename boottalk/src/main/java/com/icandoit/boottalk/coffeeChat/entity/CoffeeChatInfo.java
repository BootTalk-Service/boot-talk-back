package com.icandoit.boottalk.coffeeChat.entity;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.UserType;
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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coffee_chat_info")
public class CoffeeChatInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coffeeChatInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Column(nullable = false)
    private String introduction;

    @Column(nullable = false)
    private Boolean deleted = false;

    @OneToMany(mappedBy = "coffeeChatInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoffeeChatTime> availableTimes = new ArrayList<>();

    public static CoffeeChatInfo of(User user, String userType, String jobType,
        String introduction) {
        return CoffeeChatInfo.builder()
            .user(user)
            .userType(UserType.valueOf(userType))
            .jobType(JobType.valueOf(jobType))
            .introduction(introduction)
            .deleted(false)
            .build();
    }

    public void update(CoffeeChatInfoRequestDto requestDto) {
        this.userType = UserType.valueOf(requestDto.userType());
        this.jobType = JobType.valueOf(requestDto.jobType());
        this.introduction = requestDto.introduction();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void delete() {
        this.deleted = Boolean.TRUE;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public void addAvailableTime(CoffeeChatTime time) {
        availableTimes.add(time);
        time.setCoffeeChatInfo(this);
    }
}