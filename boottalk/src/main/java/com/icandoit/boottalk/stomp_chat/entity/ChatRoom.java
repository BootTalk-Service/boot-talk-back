package com.icandoit.boottalk.stomp_chat.entity;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private CoffeeChatApplication coffeeChatApplication;

    @Column(nullable = false)
    private LocalDateTime reservationAt; // 커피챗 시작시간

    @Column(nullable = false)
    private LocalDateTime endAt; // 예약종료 시간

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 채팅방 보존 기간 후 삭제 예정 시간

    @Column(nullable = false)
    @Setter
    private boolean isActive; // 채팅방 활성화 여부

    @Column(nullable = false)
    @Setter
    private boolean mentorEntered; // 멘토 접속 여부 확인 -> 미접속 시 알림

    @Column(nullable = false)
    @Setter
    private boolean menteeEntered; // 멘티 접속 여부 확인 -> 미접속 시 알림

    @Column(nullable = false)
    private boolean hasNewMessages;


    public static ChatRoom of(CoffeeChatApplication coffeeChatApplication) {

        LocalDateTime startTime = coffeeChatApplication.getCoffeeChatStartTime();
        return ChatRoom.builder()
            .roomUuid(UUID.randomUUID().toString())
            .coffeeChatApplication(coffeeChatApplication)
            .reservationAt(startTime)
            .endAt(startTime.plusMinutes(30))
            .expiresAt(startTime.plusDays(7)) // 7일 후 삭제
            .isActive(true)
            .mentorEntered(false)
            .menteeEntered(false)
            .hasNewMessages(false)
            .build();
    }
}
