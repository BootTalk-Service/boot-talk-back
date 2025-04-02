package com.icandoit.boottalk.bootcamp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.bootcamp.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
	Optional<Course> findByTrainingProgramId(String trainingProgramId);
}
