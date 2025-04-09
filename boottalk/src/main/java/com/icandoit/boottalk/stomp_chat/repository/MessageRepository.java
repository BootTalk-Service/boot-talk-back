package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.stomp_chat.entity.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomUuid(String roomUuid);
}
