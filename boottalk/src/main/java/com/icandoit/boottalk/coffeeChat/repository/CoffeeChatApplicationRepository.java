package com.icandoit.boottalk.coffeeChat.repository;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoffeeChatApplicationRepository extends JpaRepository<CoffeeChatApplication, Long> {

	List<CoffeeChatApplication> findByMentee_UserId(Long userId);

	// Param으로 받은 userId에 따라 멘토 or 멘티의 예약된 커피챗 리스트 조회
	@Query("""
    SELECT ca FROM CoffeeChatApplication ca
    WHERE (ca.mentee.userId = :userId OR ca.coffeeChatInfo.mento.userId = :userId)
    AND ca.status = 'APPROVED'
""")
	Page<CoffeeChatApplication> findApprovedChatsByUserId(@Param("userId") Long userId, Pageable pageable);
}
