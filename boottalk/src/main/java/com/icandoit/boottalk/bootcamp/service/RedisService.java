package com.icandoit.boottalk.bootcamp.service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public static final String BOOTCAMP_KEY_PREFIX = "new:bootcamp:";

	// redis 에서 pattern 에 해당하는 key 값 조회
	public Set<String> getKeys(String pattern) {
		Set<String> keys = redisTemplate.keys(pattern);
		if (CollectionUtils.isEmpty(keys)) {
			log.info("패턴 [{}]에 해당하는 키가 없습니다.", pattern);
		} else {
			log.info("패턴 [{}]에 해당하는 키들: {}", pattern, keys);
		}
		return keys;
	}

	// redis 에서 키값에 해당하는 데이터 조회
	public List<String> getList(String key) {
		List<String> list = redisTemplate.opsForList().range(key, 0, -1);
		log.info("키 [{}]에 저장된 리스트 값: {}", key, list);
		return list;
	}

	// redis 에 key 와 value 값 저장
	public void storeNewBootcampInfo(String bootcampId, BootcampCategoryType categoryType) {
		String redisKey = BOOTCAMP_KEY_PREFIX + categoryType.name();
		redisTemplate.opsForList().rightPush(redisKey, bootcampId);
		redisTemplate.expire(redisKey, Duration.ofHours(2));
	}

	// redis 에서 해당 키 값 삭제
	public void deleteKey(String redisKey) {
		Boolean deleted = redisTemplate.delete(redisKey);
		if (Boolean.TRUE.equals(deleted)) {
			log.info("키 [{}] 삭제 성공", redisKey);
		} else {
			log.warn("키 [{}] 삭제 실패 또는 존재하지 않음", redisKey);
		}
	}
}
