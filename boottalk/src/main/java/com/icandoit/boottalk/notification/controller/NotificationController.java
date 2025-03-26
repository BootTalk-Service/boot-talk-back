package com.icandoit.boottalk.notification.controller;

import com.icandoit.boottalk.notification.dto.NotificationRequest;
import com.icandoit.boottalk.notification.dto.NotificationResponse.NotificationResponseDto;
import com.icandoit.boottalk.notification.dto.NotificationResponse.Response;
import com.icandoit.boottalk.notification.entity.Notification;
import com.icandoit.boottalk.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@PostMapping
	public ResponseEntity<?> createNotification(
		@RequestBody NotificationRequest request
	) {
		try {
			Notification notification = notificationService.save(request);
			NotificationResponseDto responseDto = new NotificationResponseDto(notification);

			return ResponseEntity.ok(new Response("알림이 성공적으로 생성되었습니다.", responseDto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new Response(e.getMessage(), null));
		}
	}

	@GetMapping("/{notificationId}")
	public ResponseEntity<?> getNotificationByNotificationId(
		@PathVariable Long notificationId
	) {
		try {
			Notification notification = notificationService.findById(notificationId);
			NotificationResponseDto responseDto = new NotificationResponseDto(notification);
			return ResponseEntity.ok(new Response("알림 조회에 성공했습니다.", responseDto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new Response(e.getMessage(), null));
		}
	}

	@GetMapping("/my/{userId}")
	public ResponseEntity<?> getNotificationByUserId(
		@PathVariable Long userId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<Notification> notificationPage = notificationService.findAllByUserId(userId,
				pageable);
			List<NotificationResponseDto> notificationResponseDtoList =
				notificationPage.getContent().stream()
					.map(NotificationResponseDto::new)
					.toList();

			return ResponseEntity.ok(
				new Response("사용자의 알림 목록 전체 조회에 성공했습니다.", notificationResponseDtoList)
			);
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new Response(e.getMessage(), null));
		}
	}

	@PutMapping("/{notificationId}")
	public ResponseEntity<?> updateNotification(
		@PathVariable Long notificationId,
		@RequestBody NotificationRequest request
	) {
		try {
			Notification updatedNotification = notificationService.update(notificationId, request);
			NotificationResponseDto responseDto = new NotificationResponseDto(updatedNotification);
			return ResponseEntity.ok(new Response("알림 수정에 성공했습니다.", responseDto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new Response(e.getMessage(), null));
		}
	}

	@DeleteMapping("/{notificationId}")
	public ResponseEntity<?> deleteNotification(
		@PathVariable Long notificationId
	) {
		try {
			notificationService.delete(notificationId);
			return ResponseEntity.ok(new Response("알림 삭제에 성공했습니다.", null));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(new Response("알림 삭제에 실패했습니다.", null));
		}
	}
}
