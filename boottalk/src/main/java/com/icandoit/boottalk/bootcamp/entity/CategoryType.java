package com.icandoit.boottalk.bootcamp.entity;

import java.util.HashMap;
import java.util.Map;

public enum CategoryType {
  //TODO : 추후에 카테고리들 더 추가 해야 함
  APPLICATION_SW_ENGINEERING("응용SW엔지니어링"),
  SMART_EQUIPMENT_DESIGN("스마트설비설계"),
  INFORMATION_SECURITY_MANAGEMENT("정보보호관리·운영"),
  SW_PRODUCT_PLANNING("SW제품기획");

  private final String koreanName;
  private static final Map<String, String> englishToKoreanMap = new HashMap<>();
  private static final Map<String, CategoryType> koreanToEnumMap = new HashMap<>();

  CategoryType(String koreanName) {
    this.koreanName = koreanName;
  }

  static {
    for (CategoryType category : CategoryType.values()) {
      englishToKoreanMap.put(category.name(), category.koreanName);
      koreanToEnumMap.put(category.koreanName, category);
    }

  }

  public String getKoreanNameByEnglishName(String englishName) {
    return englishToKoreanMap.getOrDefault(englishName, "알 수 없음");
  }

  public static CategoryType fromKoreanName(String koreanName) {
    if(!koreanToEnumMap.containsKey(koreanName)) {
      throw new IllegalArgumentException("지원하지 않는 카테고리 이름입니다.");
    }
    return koreanToEnumMap.get(koreanName);
  }
}
