package com.icandoit.boottalk.notification.repository;

import com.icandoit.boottalk.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Page<Notification> findAllByUser_UserId(Long userId, Pageable pageable);
}
