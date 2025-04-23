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
        // 캐시 조회: DTO로 가져옴
        List<ChatMessageResponseDto> cachedDtos = redisRepository.getMessages(roomUuid);

        if (cachedDtos.isEmpty()) {
            // DB에서 조회: Entity로 가져옴
            List<ChatMessage> dbMessages = messageRepository.findByRoomUuid(roomUuid);

            if (!dbMessages.isEmpty()) {
                // Entity -> DTO 변환
                cachedDtos = dbMessages.stream()
                    .map(ChatMessageResponseDto::from)
                    .toList();

                // Redis에 DTO 저장
                redisRepository.saveAll(roomUuid, cachedDtos, Duration.ofMinutes(30));
                System.out.println("[Server] DB에서 메시지 가져와 Redis 저장 완료: " + cachedDtos.size() + "건");
            } else {
                System.out.println("[Server] Redis 에서 메시지 조회 성공: " + cachedDtos.size() + "건");
            }
        }

        // WebSocket으로 전송 (항상 DTO 사용)
        String destination = "/queue/chat/" + roomUuid + "/" + userId;
        System.out.println("[Server] WebSocket 메시지 전송 시도: destination=" + destination + ", 메시지 수="
            + cachedDtos.size());
        for (ChatMessageResponseDto dto : cachedDtos) {
            template.convertAndSend(destination, dto);
        }
    }
}