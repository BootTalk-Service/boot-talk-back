package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.user.domain.entity.User;

public record ChatUserDto(
    Long userId,
    String name,
    String profileImage
) {

    public static ChatUserDto from(User user) {
        return new ChatUserDto(
            user.getUserId(),
            user.getUserName(),
            user.getProfileImage()
        );
    }
}
