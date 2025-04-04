package com.icandoit.boottalk.user.domain.repository;

import java.util.Optional;

import com.icandoit.boottalk.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	//해당 id를 기준으로 삭제일이 없는 유저만 반환
	Optional<User> findByResourceUserIdAndDeletedAtIsNull(String resourceUserId);
}
