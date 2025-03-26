package com.icandoit.boottalk.notification.dto;

import com.icandoit.boottalk.common.dto.BaseResponse;
import com.icandoit.boottalk.notification.entity.Notification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

public class NotificationResponse {

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = true)
	public static class Response extends BaseResponse {

		public Response(String Message) {
			super(Message, null);
		}

		public Response(String message, Object data) {
			super(message, data);
		}
	}

	@Getter
	@Setter
	public static class NotificationResponseDto {

		private Long id;
		private Long userId;
		private String type;
		private String message;
		private boolean isRead;

		public NotificationResponseDto(Notification notification) {
			this.id = notification.getId();
			this.userId = notification.getUser().getUserId();
			this.type = String.valueOf(notification.getType());
			this.message = notification.getMessage();
			this.isRead = notification.isRead();
		}
	}

}
