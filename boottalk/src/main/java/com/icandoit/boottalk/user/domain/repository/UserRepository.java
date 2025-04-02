package com.icandoit.boottalk.user.domain.repository;

import java.util.Optional;

import com.icandoit.boottalk.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByResourceUserId(String resourceUserId);
}
