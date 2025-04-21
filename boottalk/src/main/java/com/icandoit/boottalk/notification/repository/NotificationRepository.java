package com.icandoit.boottalk.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC LIMIT 10")
	List<Notification> findAllNotificationByUserId(@Param("userId") long userId);

	@Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt > :time")
	List<Notification> findByMissedNotifications(@Param("userId")long userId, @Param("time") LocalDateTime time);


	@Modifying
	@Transactional
	@Query("UPDATE Notification n SET n.checked = true " +
		"WHERE n.userId = :userId AND n.checked = false AND n.createdAt <= :time")
	int checkedAllNotification(@Param("userId") long userId, @Param("time") LocalDateTime time);
}
