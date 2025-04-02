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
	SW_PRODUCT_PLANNING("SW제품기획"),
	BIGDATA_ANALYSIS("빅데이터분석"),
	AI_SERVICE_IMPLEMENTATION("인공지능서비스구현"),
	UI_UX_ENGINEERING("UI/UX엔지니어링"),
	SECURITY_ENGINEERING("보안엔지니어링"),
	ELECTRONIC_HARDWARE_DEV("전자응용기기하드웨어개발"),
	DIGITAL_BIZ_SUPPORT_SERVICE("디지털비즈니스지원서비스"),
	SEMICONDUCTOR_DEV("반도체개발"),
	CLOUD_INFRA_ENGINEERING("클라우드인프라스트럭쳐엔지니어링"),
	BIGDATA_PLATFORM_DEV("빅데이터플랫폼구축"),
	DB_ENGINEERING("DB엔지니어링"),
	CLOUD_SOLUTION_ARCH("클라우드솔루션아키텍처"),
	IOT_SYSTEM_INTEGRATION("IoT시스템연동");

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

	// TODO : 추후에 리스트 조회시 사용할 예정
	public String getKoreanNameByEnglishName(String englishName) {
		return englishToKoreanMap.getOrDefault(englishName, "알 수 없음");
	}

	public static boolean isValidKoreanName(String koreanName) {
		return koreanToEnumMap.containsKey(koreanName);
	}

	public static BootcampCategoryType fromKoreanName(String koreanName) {
		if(!koreanToEnumMap.containsKey(koreanName)) {
			throw new BootcampCustomException(BootcampErrorCode.INVALID_CATEGORY_NAME);
		}
		return koreanToEnumMap.get(koreanName);
	}
}
