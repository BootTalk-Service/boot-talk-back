package com.icandoit.boottalk.stomp_chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.stomp_chat.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	List<Message> findByChatRoom_ChatRoomId(Long chatRoomId);

}
