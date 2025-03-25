package com.icandoit.boottalk.bootcamp.entity;

import java.util.HashMap;
import java.util.Map;

public enum CategoryType {
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
    return koreanToEnumMap.get(koreanName);
  }
}
