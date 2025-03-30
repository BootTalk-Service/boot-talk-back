package com.icandoit.boottalk.bootcamp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;

public interface TrainingCenterRepository extends JpaRepository<TrainingCenter, Long> {

	Optional<TrainingCenter> findByName(String tcName);
}
