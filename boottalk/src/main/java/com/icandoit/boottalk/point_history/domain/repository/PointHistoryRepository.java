package com.icandoit.boottalk.point_history.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.point_history.domain.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

	Page<PointHistory> findAllByUserId(long userId, Pageable pageable);

	Optional<PointHistory> findTopByUserIdOrderByPointHistoryIdDesc(long userId);
}
