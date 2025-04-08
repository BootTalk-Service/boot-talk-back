package com.icandoit.boottalk.bootcamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;

public interface BootcampCertificationRepository extends JpaRepository<BootcampCertification, Long> {
}
