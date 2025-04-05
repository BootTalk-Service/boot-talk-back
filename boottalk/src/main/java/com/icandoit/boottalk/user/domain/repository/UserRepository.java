package com.icandoit.boottalk.user.domain.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.icandoit.boottalk.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	//해당 id를 기준으로 삭제일이 없는 유저만 반환
	Optional<User> findByResourceUserIdAndDeletedAtIsNull(String resourceUserId);

	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.resourceUserId = :resourceUserId AND u.deletedAt >= :sixMonthsAgo")
	boolean existsByResourceUserIdAndDeletedWithinSixMonths(
		@Param("resourceUserId") String resourceUserId,
		@Param("sixMonthsAgo") Timestamp time
	);

	//테스트용
	Optional<User> findByUserName(String userName);
}
