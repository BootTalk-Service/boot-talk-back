package com.icandoit.boottalk.coffeeChat.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.icandoit.boottalk.coffeeChat.service.CoffeeChatRefundService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CoffeeChatRefundScheduler {

    private final CoffeeChatRefundService coffeeChatRefundService;

    // @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    @Scheduled(cron = "0 * * * * *")
    public void runCoffeeChatRefundJob() {
        coffeeChatRefundService.refundExpiredPendingChats();
    }

}
