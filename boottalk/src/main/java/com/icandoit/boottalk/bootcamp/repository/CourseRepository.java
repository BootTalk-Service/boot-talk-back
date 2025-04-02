package com.icandoit.boottalk.bootcamp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.bootcamp.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
	// trainingProgramId(훈련과정 ID)를 기준으로 Course 조회.
	Optional<Course> findByTrainingProgramId(String trainingProgramId);
}
