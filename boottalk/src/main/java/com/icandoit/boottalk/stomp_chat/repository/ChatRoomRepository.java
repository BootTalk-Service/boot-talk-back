package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
            SELECT c
            FROM ChatRoom c
            JOIN FETCH c.roomStatus
            JOIN c.coffeeChatApplication app
            JOIN app.coffeeChatInfo info
            WHERE app.mentee.userId = :userId OR info.mentor.userId = :userId
            ORDER BY c.endAt DESC
        """)
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);

    Optional<ChatRoom> findByRoomUuid(String roomUuid);

}
