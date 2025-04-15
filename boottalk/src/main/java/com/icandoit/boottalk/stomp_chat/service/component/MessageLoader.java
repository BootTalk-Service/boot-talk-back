package com.icandoit.boottalk.stomp_chat.service.component;

import com.icandoit.boottalk.stomp_chat.dto.MessageResponseDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.repository.ChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.RedisChatMessageRepository;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class MessageLoader {

    private final RedisChatMessageRepository redisRepository;
    private final ChatMessageRepository messageRepository;
    private final SimpMessageSendingOperations template;

    @Transactional(readOnly = true)
    public void loadAndSendMessages(Long userId, String roomUuid) {
        // 캐시 조회
        List<ChatMessage> chatMessages = redisRepository.getMessages(roomUuid);

        if (chatMessages.isEmpty()) {
            // 캐시에 없다면 DB에서 조회
            chatMessages = messageRepository.findByRoomUuid(roomUuid);

            if (!chatMessages.isEmpty()) {
                // Redis에 캐싱 - 30분 TTL
                redisRepository.saveAll(roomUuid, chatMessages, Duration.ofMinutes(30));
            }
        }

        List<MessageResponseDto> responseDtos = chatMessages.stream()
            .map(MessageResponseDto::from)
            .toList();

        // WebSocket으로 전송
        String destination = "/queue/chat/" + roomUuid + "/" + userId;
        template.convertAndSend(destination, responseDtos);
    }
}