package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.repository.ChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.RedisChatMessageRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<String> keys = redisTemplate.keys("chat:room:info:*");

        if (keys == null || keys.isEmpty()) {
            return;
        }

        Set<String> roomUuids = keys.stream()
            .map(key -> key.replace("chat:room:info:", ""))
            .collect(Collectors.toSet());

        for (String roomUuid : roomUuids) {
            // DTO로 가져오기
            List<ChatMessageResponseDto> messagesToFlushDto = redisChatRepository.getMessagesForBatch(
                roomUuid);

            if (!messagesToFlushDto.isEmpty()) {
                // DTO → Entity 변환
                List<ChatMessage> messagesToFlush = messagesToFlushDto.stream()
                    .map(ChatMessageResponseDto::toEntity)
                    .toList();

                // DB 저장
                chatMessageRepository.saveAll(messagesToFlush);

                // Redis에서 앞부분 잘라내기 (DTO 기준으로)
                redisChatRepository.deleteMessages(roomUuid, messagesToFlushDto.size());

                log.info("Scheduled : [roomUuid: {}] Redis에서 DB로 메시지 {}개 저장됨", roomUuid,
                    messagesToFlush.size());
            }
        }
    }
}