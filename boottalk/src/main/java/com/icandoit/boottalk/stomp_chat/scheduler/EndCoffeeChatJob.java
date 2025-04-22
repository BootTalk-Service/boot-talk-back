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
public class EndCoffeeChatJob extends QuartzJobBean {

    private final ChatRoomService chatRoomService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            String roomUuid = context.getMergedJobDataMap().getString("roomUuid");
            chatRoomService.endCoffeeChat(roomUuid);
        } catch (Exception e) {
            // 예외가 발생하면 RuntimeException을 던진다.
            throw new JobExecutionException("Error while ending coffee chat", e);
        }
    }
}