package com.icandoit.boottalk.stomp_chat.entity;

import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(nullable = false)
    private String roomUuid;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    public static Message of(String roomUuid, Long senderId, String senderName, Long receiverId, String message,
        MessageType type) {
        return Message.builder()
            .roomUuid(roomUuid)
            .senderId(senderId)
            .senderName(senderName)
            .receiverId(receiverId)
            .message(message)
            .sentAt(LocalDateTime.now())
            .isRead(false)
            .type(type)
            .build();
    }
}