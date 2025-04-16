package com.icandoit.boottalk.stomp_chat.service.component;

import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
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
        // 1. 캐시 조회: DTO로 가져옴
        List<ChatMessageResponseDto> cachedDtos = redisRepository.getMessages(roomUuid);

        if (cachedDtos.isEmpty()) {
            // 2. DB에서 조회: Entity로 가져옴
            List<ChatMessage> dbMessages = messageRepository.findByRoomUuid(roomUuid);

            if (!dbMessages.isEmpty()) {
                // 3. Entity -> DTO 변환
                cachedDtos = dbMessages.stream()
                    .map(ChatMessageResponseDto::from)
                    .toList();

                // 4. Redis에 DTO 저장
                redisRepository.saveAll(roomUuid, cachedDtos, Duration.ofMinutes(30));
            }
        }

        // 5. WebSocket으로 전송 (항상 DTO 사용)
        String destination = "/queue/chat/" + roomUuid + "/" + userId;
        template.convertAndSend(destination, cachedDtos);
    }
}