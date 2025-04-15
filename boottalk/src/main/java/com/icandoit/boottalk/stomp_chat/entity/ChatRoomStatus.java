package com.icandoit.boottalk.stomp_chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "chat_room_status")
public class ChatRoomStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomStatusId;

    @OneToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    @Setter
    private boolean isActive; // 채팅방 활성화 여부

    @Column(nullable = false)
    @Setter
    private boolean mentorEntered; // 멘토 접속 여부

    @Column(nullable = false)
    @Setter
    private boolean menteeEntered; // 멘티 접속 여부

    @Column(nullable = false)
    @Setter
    private boolean hasNewMessages; // 새 메시지 여부

    public static ChatRoomStatus of(ChatRoom chatRoom, boolean active, boolean mentorEntered,
        boolean menteeEntered, boolean hasNewMessages) {
        return ChatRoomStatus.builder()
            .chatRoom(chatRoom)
            .isActive(active)
            .mentorEntered(mentorEntered)
            .menteeEntered(menteeEntered)
            .hasNewMessages(hasNewMessages)
            .build();
    }
}
