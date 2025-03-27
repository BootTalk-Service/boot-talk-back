package com.icandoit.boottalk.coffeeChat.repository;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeChatInfoRepository extends JpaRepository<CoffeeChatInfo, Long> {

    Optional<CoffeeChatInfo> findByUserId(Long userId);
}
