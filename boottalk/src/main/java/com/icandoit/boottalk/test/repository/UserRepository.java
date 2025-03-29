package com.icandoit.boottalk.test.repository;

import com.icandoit.boottalk.test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsById(Long id);
}
