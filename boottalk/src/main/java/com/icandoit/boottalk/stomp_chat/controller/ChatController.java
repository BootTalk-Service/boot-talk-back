package com.icandoit.boottalk.stomp_chat.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.icandoit.boottalk.stomp_chat.dto.ChatRoomEnterDto;
import com.icandoit.boottalk.stomp_chat.dto.MessageDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.entity.Message;
import com.icandoit.boottalk.stomp_chat.entity.User;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.repository.MessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate template;

	private final MessageRepository messageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final UserRepository userRepository;


	@MessageMapping("/chat/enter")
	public void enter(@Payload ChatRoomEnterDto chatRoomEnterDto) {
		// 채팅방 입장 시, 해당 채팅방의 이전 대화 목록 출력
		List<Message> previousMessages = messageRepository.findByChatRoom_ChatRoomId(chatRoomEnterDto.getChatRoomId());

		List<MessageDto> previousMessagesDto = previousMessages.stream()
				.map(message -> MessageDto.from(message))
				.collect(Collectors.toList());

		String destination = "/queue/chat/" + chatRoomEnterDto.getChatRoomId() + "/" + chatRoomEnterDto.getEnterUserId();
		template.convertAndSend(destination, previousMessagesDto);
	}



	/*
		1:1 채팅 메시지 처리 (메시지를 처리하고 특정 사용자에게 전송)
			- 클라이언트가 /app/chat/send/ 로 메시지를 보내면 호출됨
			- /app: 클라이언트가 WebSocket 을 통해 서버에 메시지를 보낼 때 사용되는 기본 경로로, 서버에서 @MessageMapping 을 처리하는 메소드로 라우팅됨
			- /queue: 서버가 클라이언트에게 메시지를 보낼 때 사용하는 경로로, 주로 특정 클라이언트나 사용자에게 메시지를 전송하는데 사용됨
	 */
	@MessageMapping("/chat/send") // STOMP 에서 @MessageMapping 은 REST API 의 @PostMapping 과 비슷한 역할
	public void sendMessage(@Payload MessageDto message) {
		log.info("메시지 수신: user{} -> user{} : {}", message.getSenderId(), message.getReceiverId(), message.getMessage());

		User sender = getUser(message.getSenderId());
		User receiver = getUser(message.getReceiverId());
		ChatRoom chatRoom = chatRoomRepository.findById(message.getChatRoomId()).orElseThrow();
		messageRepository.save(Message.of(message, sender, receiver, chatRoom)); // 메시지 저장
		// TODO: 실시간 채팅에서 메시지 전송할 때마다 매번 user, chatRoom 조회는 비효율적 -> 캐싱 활용하여 조회 최적화 필요

		// /queue/chat/{chat_room_id}/{receiver_id} 경로를 구독하는 클라이언트에게 메시지를 전송
		String destination = "/queue/chat/" + message.getChatRoomId() + "/" + message.getReceiverId();
		template.convertAndSend(destination, message); // 메시지 전송

		log.info("메시지 전송 완료: {}", destination);
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId).orElseThrow();
	}


}