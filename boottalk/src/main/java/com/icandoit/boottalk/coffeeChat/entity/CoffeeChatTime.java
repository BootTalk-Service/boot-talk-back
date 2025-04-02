package com.icandoit.boottalk.coffeeChat.entity;

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
import java.time.DayOfWeek;
import java.time.LocalTime;
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
@Table(name = "coffee_chat_time")
public class CoffeeChatTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coffeeChatTimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coffee_chat_info_id")
    private CoffeeChatInfo coffeeChatInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    // 생성 메서드
    public static CoffeeChatTime of(CoffeeChatInfo coffeeChatInfo, DayOfWeek dayOfWeek,
        LocalTime startTime, LocalTime endTime) {
        return CoffeeChatTime.builder()
            .coffeeChatInfo(coffeeChatInfo)
            .dayOfWeek(dayOfWeek)
            .startTime(startTime)
            .endTime(endTime)
            .build();
    }


    void setCoffeeChatInfo(CoffeeChatInfo coffeeChatInfo) {
        this.coffeeChatInfo = coffeeChatInfo;
    }
}