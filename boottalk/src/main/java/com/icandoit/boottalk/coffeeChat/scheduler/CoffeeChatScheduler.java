package com.icandoit.boottalk.coffeeChat.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.icandoit.boottalk.coffeeChat.service.CoffeeChatScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CoffeeChatScheduler {

    private final CoffeeChatScheduleService coffeeChatScheduleService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void runCoffeeChatRefundJob() {
        coffeeChatScheduleService.refundExpiredPendingChats();
    }

    @Scheduled(cron = "0 0/30 * * * *") // 30분마다 실행
    public void sendCoffeeChatRemindersEvery30Minutes() {
        coffeeChatScheduleService.sendCoffeeChatRemindersNext30Minutes();
    }

}
