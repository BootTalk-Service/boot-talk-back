package com.icandoit.boottalk.stomp_chat.scheduler;

import com.icandoit.boottalk.stomp_chat.service.ChatRoomService;
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

    private final ChatRoomService chatRoomService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String roomUuid = context.getMergedJobDataMap().getString("roomUuid");

        chatRoomService.activateChatRoom(roomUuid);
    }
}