package com.icandoit.boottalk.point_history.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.icandoit.boottalk.point_history.domain.entity.PointHistory;

import jakarta.persistence.LockModeType;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

	Page<PointHistory> findAllByUserId(long userId, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<PointHistory> findTopByUserIdOrderByPointHistoryIdDesc(long userId);
}
