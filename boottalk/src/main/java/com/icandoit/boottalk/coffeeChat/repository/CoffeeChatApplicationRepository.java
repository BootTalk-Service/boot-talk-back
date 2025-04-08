package com.icandoit.boottalk.coffeeChat.repository;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface CoffeeChatApplicationRepository extends JpaRepository<CoffeeChatApplication, Long> {

	Page<CoffeeChatApplication> findByMentee_UserId(Long userId, Pageable pageable);

	Page<CoffeeChatApplication> findByCoffeeChatInfo_CoffeeChatInfoId(Long userId, Pageable pageable);

	// Param으로 받은 userId에 따라 멘토 or 멘티의 예약된 커피챗 리스트 조회
	@Query("""
    SELECT ca FROM CoffeeChatApplication ca
    WHERE (ca.mentee.userId = :userId OR ca.coffeeChatInfo.mentor.userId = :userId)
    AND ca.status = 'APPROVED'
""")
	Page<CoffeeChatApplication> findApprovedChatsByUserId(@Param("userId") Long userId, Pageable pageable);

	// 특정 유저가 해당 커피챗에 대해 이미 신청 중인 상태인지 확인
	// (가장 최신 신청 상태가 대기 또는 수락인 경우 중복 신청으로 간주)
	@Query("""
		SELECT CASE WHEN COUNT(ca) > 0 THEN true ELSE false END
		FROM CoffeeChatApplication ca
		WHERE ca.mentee.userId = :userId
		AND ca.coffeeChatInfo.coffeeChatInfoId = :coffeeChatInfoId
		AND ca.status IN ('PENDING', 'APPROVED')
		ORDER BY ca.id DESC LIMIT 1
	""")
	boolean existsLatestPendingOrApprovedApplication(
		@Param("userId") Long userId,
		@Param("coffeeChatInfoId") Long coffeeChatInfoId);

	// 해당 시간대에 이미 다른 신청자가 대기 또는 수락 상태로 신청한 내역이 있는지 확인
	// (중복 예약 방지를 위해 Lock 처리)
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
		SELECT CASE WHEN COUNT(ca) > 0 THEN true ELSE false END
		FROM CoffeeChatApplication ca
		WHERE ca.coffeeChatInfo.coffeeChatInfoId = :coffeeChatInfoId
		AND ca.coffeeChatStartTime = :coffeeChatStartTime
		AND ca.status IN ('PENDING', 'APPROVED')
	""")
	boolean isTimeSlotAlreadyTaken(
		@Param("coffeeChatInfoId") Long coffeeChatInfoId,
		@Param("coffeeChatStartTime") LocalDateTime coffeeChatStartTime);

	boolean existsByMentee_UserIdAndCoffeeChatInfo_CoffeeChatInfoId(Long userId, Long coffeeChatInfoId);

}
