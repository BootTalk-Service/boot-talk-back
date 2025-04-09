package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByCoffeeChatApplication(CoffeeChatApplication application);

    @Query("""
    SELECT c
    FROM ChatRoom c
    JOIN c.coffeeChatApplication app
    JOIN app.coffeeChatInfo info
    WHERE c.isActive = true AND (
        app.mentee.userId = :userId OR info.mentor.userId = :userId
    )
""")
    List<ChatRoom> findActiveChatRoomsByUserId(@Param("userId") Long userId);

    Optional<ChatRoom> findByRoomUuid(String roomUuid);

}
