package com.icandoit.boottalk.stomp_chat.entity;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat_room")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @Column(nullable = false, unique = true)
    private String roomUuid;  // UUID 형식의 고유 식별자 -> 프론트 노출용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private CoffeeChatApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id", nullable = false)
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_user_id", nullable = false)
    private User mentee;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;  // 30분 제한 시간

    @Column(nullable = false)
    private LocalDateTime deletionScheduledAt; // 채팅방 보존 기간 후 삭제 예정 시간

    @Column(nullable = false)
    @Setter
    private boolean isActive; // 채팅방 활성화 여부

    @Column(nullable = false)
    @Setter
    private boolean mentorEntered; // 멘토 접속 여부 확인 -> 비접속 시 알림 및 받은 메시지 개수 카운팅

    @Column(nullable = false)
    @Setter
    private boolean menteeEntered; // 멘티 접속 여부 확인 -> 비접속 시 알림 및 받은 메시지 개수 카운팅

    @Column(nullable = false)
    private int mentorUnreadCount; // 메시지 갯수 카운트

    @Column(nullable = false)
    private int menteeUnreadCount;

    public static ChatRoom of(CoffeeChatApplication application) {

        LocalDateTime reservationTime = application.getCoffeeChatStartTime();
        return ChatRoom.builder()
            .roomUuid(UUID.randomUUID().toString())
            .application(application)
            .mentor(application.getCoffeeChatInfo().getMentor())
            .mentee(application.getMentee())
            .createdAt(reservationTime)
            .expiresAt(reservationTime.plusMinutes(30))
            .deletionScheduledAt(reservationTime.plusDays(7)) // 7일 후 삭제
            .isActive(true)
            .mentorEntered(false)
            .menteeEntered(false)
            .mentorUnreadCount(0)
            .menteeUnreadCount(0)
            .build();
    }
}
