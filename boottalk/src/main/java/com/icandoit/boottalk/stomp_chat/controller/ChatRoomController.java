package com.icandoit.boottalk.stomp_chat.controller;

import com.icandoit.boottalk.stomp_chat.dto.ChatRoomCreateResponse;
import com.icandoit.boottalk.stomp_chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성 ( 멘토가 커피챗 신청 승인 시 호출 예정 )
    @PostMapping("/{applicationId}")
    public ResponseEntity<ChatRoomCreateResponse> createRoom(@PathVariable Long applicationId) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(applicationId));
    }


}
