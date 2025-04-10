package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.stomp_chat.dto.MessageRequestDto;
import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;
import org.springframework.stereotype.Component;

@Component
public class MessageFactoryService {

    private static final Long SYSTEM_SENDER_ID = 0L;
    private static final String SYSTEM_SENDER_NAME = "SYSTEM";

    public MessageRequestDto createSystemMessage(String roomUuid, Long receiverId, String content) {
        return new MessageRequestDto(
            roomUuid,
            SYSTEM_SENDER_ID,
            SYSTEM_SENDER_NAME,
            receiverId,
            content,
            MessageType.SYSTEM
        );
    }
}
