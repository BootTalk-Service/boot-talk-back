package com.icandoit.boottalk.coffeeChat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;

public interface CoffeeChatApplicationRepository extends JpaRepository<CoffeeChatApplication, Long> {

	List<CoffeeChatApplication> findByApplier_UserId(Long userId);
}
