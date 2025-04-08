package com.icandoit.boottalk.stomp_chat.entity;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.user.domain.entity.User;
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

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    private String roomUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id")
    private User mentee;

    private LocalDateTime reservationTime;
    @OneToOne
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private CoffeeChatApplication coffeeChatApplication;

    private boolean isExited;

    public static ChatRoom of(CoffeeChatApplication application) {
        return ChatRoom.builder()
            .roomUuid(UUID.randomUUID().toString())
            .mentor(application.getCoffeeChatInfo().getMentor())
            .mentee(application.getMentee())
            .reservationTime(application.getCoffeeChatStartTime())
            .isExited(false)
            .build();
        // todo : 마감 시간도 필요한가?
    }
}
