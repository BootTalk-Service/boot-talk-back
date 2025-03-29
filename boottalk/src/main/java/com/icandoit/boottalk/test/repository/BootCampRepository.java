package com.icandoit.boottalk.test.repository;

import com.icandoit.boottalk.test.entity.Bootcamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootCampRepository extends JpaRepository<Bootcamp, Long> {
	boolean existsById(Long id);
}
