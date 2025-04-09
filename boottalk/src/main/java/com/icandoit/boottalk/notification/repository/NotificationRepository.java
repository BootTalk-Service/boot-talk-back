package com.icandoit.boottalk.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
