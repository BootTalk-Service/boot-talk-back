package com.icandoit.boottalk.stomp_chat.controller;

import com.icandoit.boottalk.stomp_chat.service.ChatRoomService;
import java.util.UUID;
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

    @PostMapping("/{applicationId}")
    public ResponseEntity<UUID> createRoom(@PathVariable Long applicationId) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(applicationId));
    }
}
