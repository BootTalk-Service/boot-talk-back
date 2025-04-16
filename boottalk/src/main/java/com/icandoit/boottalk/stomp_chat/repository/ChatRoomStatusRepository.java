package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.stomp_chat.entity.ChatRoomStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomStatusRepository extends JpaRepository<ChatRoomStatus, Long> {

    @EntityGraph(attributePaths = {"chatRoom.mentor", "chatRoom.mentee"})
    Optional<ChatRoomStatus> findByChatRoom_RoomUuid(String roomUuid);
}
