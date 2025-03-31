package com.icandoit.boottalk.bootcamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;

public interface TrainingCenterRepository extends JpaRepository<TrainingCenter, Long> {
}
