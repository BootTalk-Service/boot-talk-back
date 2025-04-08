package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByCoffeeChatApplication(CoffeeChatApplication application);
}
