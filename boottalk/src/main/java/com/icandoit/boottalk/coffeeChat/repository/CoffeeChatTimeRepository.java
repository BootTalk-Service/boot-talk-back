package com.icandoit.boottalk.coffeeChat.repository;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoffeeChatTimeRepository extends JpaRepository<CoffeeChatTime, Long> {

    @Query("SELECT ct FROM CoffeeChatTime ct JOIN FETCH ct.coffeeChatInfo WHERE ct.coffeeChatInfo.user.userId = :userId")
    List<CoffeeChatTime> findAllWithCoffeeChatInfoByUserId(@Param("userId") Long userId);
}
