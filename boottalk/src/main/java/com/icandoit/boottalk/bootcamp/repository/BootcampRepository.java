package com.icandoit.boottalk.bootcamp.repository;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BootcampRepository extends JpaRepository <Bootcamp, Long> {

}
