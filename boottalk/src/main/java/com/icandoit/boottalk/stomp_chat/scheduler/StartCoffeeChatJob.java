package com.icandoit.boottalk.stomp_chat.scheduler;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCoffeeChatJob extends QuartzJobBean {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String roomUuid = context.getMergedJobDataMap().getString("roomUuid");

        ChatRoom room = chatRoomRepository.findByRoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        room.getRoomStatus().setActive(true);
        chatRoomRepository.save(room);
        log.info("채팅방 활성화 완료: {}", roomUuid);
    }
}