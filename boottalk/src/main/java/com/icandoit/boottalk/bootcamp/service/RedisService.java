package com.icandoit.boottalk.bootcamp.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public void storeNewBootcampInfo(String bootcampId, BootcampCategoryType categoryType) {
		String redisKey = "new:bootcamp:" + categoryType;
		redisTemplate.opsForList().rightPush(redisKey, bootcampId);
		redisTemplate.expire(redisKey, Duration.ofHours(2));
	}
}
