package com.icandoit.boottalk.bootcamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;

public interface BootcampRepository extends JpaRepository <Bootcamp, Long> {
	// trainingProgramId와 bootcampDegree(기수)를 기준으로 부트캠프 존재 여부 확인.
	boolean existsByCourse_TrainingProgramIdAndBootcampDegree(String trainingProgramId, int bootcampDegree);
}
