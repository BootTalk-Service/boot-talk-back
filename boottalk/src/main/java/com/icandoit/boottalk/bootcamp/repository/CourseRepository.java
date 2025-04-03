package com.icandoit.boottalk.bootcamp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.icandoit.boottalk.bootcamp.entity.Course;

import jakarta.persistence.LockModeType;

public interface CourseRepository extends JpaRepository<Course, Long> {
	// trainingProgramId(훈련과정 ID)를 기준으로 Course 조회.
	Optional<Course> findByTrainingProgramId(String trainingProgramId);

	// 비관적 락을 사용한 조회
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c From Course c WHERE c.trainingProgramId = :trainingProgramId")
	Optional<Course> findWithLockByTrainingProgramId(String trainingProgramId);
}
