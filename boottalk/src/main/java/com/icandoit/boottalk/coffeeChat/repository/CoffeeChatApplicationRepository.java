package com.icandoit.boottalk.coffeeChat.repository;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import org.springframework.data.jpa.repository.Query;

public interface CoffeeChatApplicationRepository extends JpaRepository<CoffeeChatApplication, Long> {

	List<CoffeeChatApplication> findByMentee_UserId(Long userId);

	@Query("SELECT ca FROM CoffeeChatApplication ca WHERE ca.applier.userId = :userId AND ca.status = 'CONFIRMED'")
	Page<CoffeeChatInfoResponseDto> findConfirmedChatsByUserId(Long userId, Pageable pageable);
}
