package com.icandoit.boottalk.notification.entity;

import com.icandoit.boottalk.libs.entity.BaseEntity;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.type.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(
	indexes = {
		@Index(name = "idx_userId", columnList = "userId")
	}
)
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long notificationId;

	long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	NotificationType type;

	Long targetId;

	boolean checked;

	public static Notification of(long userId, NotificationRequestDto dto) {
		return Notification.builder()
			.userId(userId)
			.type(dto.type())
			.targetId(dto.targetId())
			.checked(false)
			.build();
	}
}
