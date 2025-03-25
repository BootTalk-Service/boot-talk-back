package com.icandoit.boottalk.bootcamp.repository;

import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingCenterRepository extends JpaRepository<TrainingCenter, Long> {

	Optional<TrainingCenter> findByName(String tcName);
}
