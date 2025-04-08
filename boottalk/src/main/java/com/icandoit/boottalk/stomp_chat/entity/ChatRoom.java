package com.icandoit.boottalk.stomp_chat.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

	@ManyToOne
	@JoinColumn(name = "mento_id")
	private User mentor;

	@ManyToOne
	@JoinColumn(name = "mentee_id")
	private User mentee;

	@CreatedDate
	private LocalDateTime createdAt;
}
