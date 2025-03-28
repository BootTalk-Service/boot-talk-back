package com.icandoit.boottalk.point_history.domain.repository;

import com.icandoit.boottalk.point_history.domain.entity.PointHistory;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

	Page<PointHistory> findAllByUserId(long userId, Pageable pageable);

	Optional<PointHistory> findTopByUserIdOrderByPointHistoryIdDesc(long userId);
}
