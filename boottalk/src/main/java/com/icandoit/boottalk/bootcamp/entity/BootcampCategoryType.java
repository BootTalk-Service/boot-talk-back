package com.icandoit.boottalk.bootcamp.entity;

import java.util.HashMap;
import java.util.Map;

import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
import com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode;

import lombok.Getter;

@Getter
public enum BootcampCategoryType {
	//TODO : 추후에 카테고리들 더 추가 해야 함
	APPLICATION_SW_ENGINEERING("응용SW엔지니어링"),
	SMART_EQUIPMENT_DESIGN("스마트설비설계"),
	INFORMATION_SECURITY_MANAGEMENT("정보보호관리·운영"),
	SW_PRODUCT_PLANNING("SW제품기획");

	private final String koreanName;
	private static final Map<String, String> englishToKoreanMap = new HashMap<>();
	private static final Map<String, BootcampCategoryType> koreanToEnumMap = new HashMap<>();

	BootcampCategoryType(String koreanName) {
		this.koreanName = koreanName;
	}

	static {
		for (BootcampCategoryType category : BootcampCategoryType.values()) {
			englishToKoreanMap.put(category.name(), category.koreanName);
			koreanToEnumMap.put(category.koreanName, category);
		}

	}

	public String getKoreanNameByEnglishName(String englishName) {
		return englishToKoreanMap.getOrDefault(englishName, "알 수 없음");
	}

	//TODO : 부트캠프 openAPI에서 가져올 때 사용 예정
	public static BootcampCategoryType fromKoreanName(String koreanName) {
		if(!koreanToEnumMap.containsKey(koreanName)) {
			throw new BootcampCustomException(BootcampErrorCode.INVALID_CATEGORY_NAME);
		}
		return koreanToEnumMap.get(koreanName);
	}
}
