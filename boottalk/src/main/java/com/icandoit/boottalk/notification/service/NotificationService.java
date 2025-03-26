package com.icandoit.boottalk.notification.service;

import com.icandoit.boottalk.notification.dto.NotificationRequest;
import com.icandoit.boottalk.notification.entity.Notification;
import com.icandoit.boottalk.notification.entity.NotificationType;
import com.icandoit.boottalk.notification.repository.NotificationRepository;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final UserRepository userRepository;
	private final NotificationRepository repository;

	/**
	 * 알림 저장
	 *
	 * @param request NotificationRequest DTO
	 * @return Notification
	 */
	public Notification save(NotificationRequest request) {
		Notification notification = createNotification(request);
		return repository.save(notification);
	}

	/**
	 * NotificationRequest -> Notification
	 *
	 * @param request NotificationRequestDto
	 * @return Notification
	 */
	private Notification createNotification(NotificationRequest request) {
		User user = findUserByUserId(request.getUserId());

		NotificationType notificationType;

		try {
			notificationType = NotificationType.valueOf(request.getType());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("유효하지 않은 Notification Type 입니다.");
		}

		return Notification.builder()
			.user(user)
			.type(notificationType)
			.message(request.getMessage())
			.build();
	}


	/**
	 * Notification ID로 조회
	 *
	 * @param notificationId Notification_id
	 * @return Optional<Notification>
	 */
	public Notification findById(Long notificationId) {
		return findNotificationByNotificationId(notificationId);
	}

	/**
	 * 사용자 ID에 해당하는 Notification 전체 조회
	 *
	 * @param userId   user_id
	 * @param pageable pageable 정보
	 * @return Page<Notification>
	 */
	public Page<Notification> findAllByUserId(Long userId, Pageable pageable) {
		if(!userRepository.existsById(userId)) {
			throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
		}

		return repository.findAllByUser_UserId(userId, pageable);
	}

	/**
	 * Notification 수정
	 *
	 * @param notificationId notification_id
	 * @param request        Notification Request DTO
	 * @return Notification
	 */
	public Notification update(Long notificationId, NotificationRequest request) {
		Notification notification = findNotificationByNotificationId(notificationId);
		User user = findUserByUserId(request.getUserId());

		notification.setUser(user);
		notification.setMessage(request.getMessage());

		return repository.save(notification);
	}

	/**
	 * 알림 삭제
	 *
	 * @param notificationId notification_id
	 */
	@Transactional
	public void delete(Long notificationId) {
		if (!repository.existsById(notificationId)) {
			throw new IllegalArgumentException("해당 알림이 존재하지 않습니다.");
		}

		repository.deleteById(notificationId);
	}

	private User findUserByUserId(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
	}

	private Notification findNotificationByNotificationId(Long notificationId) {
		return repository.findById(notificationId)
			.orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));

	}
}
