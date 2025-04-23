package com.icandoit.boottalk.stomp_chat.entity;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.user.domain.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private String roomUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @OneToOne
    @JoinColumn(name = "coffee_chat_app_id", nullable = false)
    private CoffeeChatApplication coffeeChatApplication;

    @Column(nullable = false)
    private LocalDateTime reservationAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // 양방향 관계 설정
    @Setter
    @OneToOne(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatRoomStatus roomStatus;

    public static ChatRoom of(CoffeeChatApplication coffeeChatApplication) {
        LocalDateTime startTime = coffeeChatApplication.getCoffeeChatStartTime();

        ChatRoom chatRoom = ChatRoom.builder()
            .roomUuid(UUID.randomUUID().toString())
            .mentor(coffeeChatApplication.getCoffeeChatInfo().getMentor())
            .mentee(coffeeChatApplication.getMentee())
            .coffeeChatApplication(coffeeChatApplication)
            .reservationAt(startTime)
            .endAt(startTime.plusMinutes(30))
            .expiresAt(startTime.plusDays(7))
            .build();

        // ChatRoomStatus 생성 및 양방향 관계 설정
        ChatRoomStatus status = ChatRoomStatus.of(chatRoom, false, false, false, false);
        chatRoom.setRoomStatus(status);

        return chatRoom;
    }

    // 멘토인지 확인
    public boolean isMentor(Long userId) {
        return this.mentor.getUserId().equals(userId);
    }

    // 멘티인지 확인
    public boolean isMentee(Long userId) {
        return this.mentee.getUserId().equals(userId);
    }

    // 추가로 유용한 메서드
    public boolean isParticipant(Long userId) {
        return isMentor(userId) || isMentee(userId);
    }
}
