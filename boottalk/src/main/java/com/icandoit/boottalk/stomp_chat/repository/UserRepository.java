package com.icandoit.boottalk.stomp_chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.stomp_chat.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
