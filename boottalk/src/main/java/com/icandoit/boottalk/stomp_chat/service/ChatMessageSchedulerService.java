package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.repository.ChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.RedisChatMessageRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageSchedulerService {

    private final RedisChatMessageRepository redisChatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5분마다 실행
    public void flushRedisToDatabase() {
        Set<String> roomUuids = redisTemplate.opsForSet().members("chat:rooms");

        if (roomUuids == null || roomUuids.isEmpty()) {
            return;
        }

        for (String roomUuid : roomUuids) {
            List<ChatMessage> messagesToFlush = redisChatRepository.getMessagesForBatch(roomUuid);

            if (!messagesToFlush.isEmpty()) {
                chatMessageRepository.saveAll(messagesToFlush);
                redisChatRepository.deleteMessages(messagesToFlush);
                log.info("Scheduled : [roomUuid: {}] Redis에서 DB로 메시지 {}개 저장됨", roomUuid, messagesToFlush.size());
            }
        }
    }
}