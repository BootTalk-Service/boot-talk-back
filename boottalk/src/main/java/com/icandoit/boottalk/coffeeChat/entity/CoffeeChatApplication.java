package com.icandoit.boottalk.coffeeChat.entity;

import java.time.LocalDateTime;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationCreateDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import com.icandoit.boottalk.libs.entity.BaseEntity;
import com.icandoit.boottalk.user.domain.entity.User;

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
@Table(name = "coffee_chat_application")
public class CoffeeChatApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coffeeChatAppId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User mentee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coffee_chat_info_id", nullable = false)
    private CoffeeChatInfo coffeeChatInfo;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private StatusType status;

    @Column(nullable = false)
    private int usedPoint;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime coffeeChatStartTime;

    @Column(nullable = false)
    private LocalDateTime coffeeChatEndTime;

    public static CoffeeChatApplication of(User user, CoffeeChatInfo coffeeChatInfo, int deductionPoint, CoffeeChatApplicationCreateDto dto) {
        return CoffeeChatApplication.builder()
            .mentee(user)
            .coffeeChatInfo(coffeeChatInfo)
            .usedPoint(deductionPoint)
            .content(dto.content())
            .coffeeChatStartTime(dto.coffeeChatStartTime())
            .coffeeChatEndTime(dto.coffeeChatEndTime())
            .status(StatusType.PENDING)
            .build();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public boolean isNDaysOrMoreUntilStart(int days) {
        return LocalDateTime.now().isBefore(this.coffeeChatStartTime.minusDays(days));
    }

}