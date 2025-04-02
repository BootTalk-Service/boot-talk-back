package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.bootcamp.dto.BootcampEmploy24Response.*;
import static com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;
import com.icandoit.boottalk.bootcamp.repository.TrainingCenterRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Employ24ApiService {

	private static final String HOST = "www.work24.go.kr";
	private static final String LIST_API_PATH = "/cm/openApi/call/hr/callOpenApiSvcInfo310L01.do";
	private static final String DETAIL_API_PATH = "/cm/openApi/call/hr/callOpenApiSvcInfo310L02.do";
	// TODO : 추후에 AUTH_KEY 관리 방식 지정하기
	private static final String AUTH_KEY = "646d5bd7-af0c-42b1-94db-fd6d2c6908f7";

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate;
	private final TrainingCenterRepository trainingCenterRepository;
	private final BootcampRepository bootcampRepository;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Getter
	private final Set<String> failedCategoryNames = new HashSet<>();

	// Employ24 API 에서 카테고리/분야 조합별로 데이터를 수집 및 저장
	public void saveAllFromEmploy24() {
		List<String> categoryCodes = List.of("C0061", "C0104", "C0105");
		List<String> ncsCodes = List.of("19", "20");

		for (String categoryCode : categoryCodes) {
			for (String ncsCode : ncsCodes) {
				processCategoryAndNcs(categoryCode, ncsCode);
			}
		}
	}

	// 주어진 카테고리와 NCS 코드로 리스트 조회 후 저장 처리
	private void processCategoryAndNcs(String categoryCode, String ncsCode) {
		try {
			List<BootcampListResponseDto> list = fetchBootcampList(categoryCode, ncsCode);
			processBootcampList(list);
		} catch (Exception e) {
			log.warn("데이터 수집 실패: categoryCode={}, ncsCode={}, message={}", categoryCode, ncsCode, e.getMessage());
		}
	}

	// 중복이 아닌 경우에만 Bootcamp 저장
	private void processBootcampList(List<BootcampListResponseDto> list) {
		for (BootcampListResponseDto dto : list) {
			if (isDuplicate(dto)) {
				log.info("이미 등록된 부트캠프: {}", dto.bootcampName());
				continue;
			}
			saveBootcamp(dto);
		}
	}

	// Bootcamp 저장 (상세 정보 조회 포함)
	private void saveBootcamp(BootcampListResponseDto dto) {
		BootcampDetailResponseDto detail = fetchBootcampDetail(dto.bootcampId(), dto.bootcampDegree(), dto.trainingCenterId());

		String categoryName = detail.ncsName();
		if (!BootcampCategoryType.isValidKoreanName(categoryName)) {
			failedCategoryNames.add(categoryName);
			return;
		}

		TrainingCenter center = trainingCenterRepository.findByTrainingCenterName(dto.trainingCenterName())
			.orElseGet(() -> trainingCenterRepository.save(
				TrainingCenter.of(
					dto.trainingCenterName(),
					detail.trainingCenterEmail(),
					detail.address1() + " " + detail.address2(),
					detail.trainingCenterUrl(),
					detail.trainingCenterTelephoneNumber()
				)
			));

		BootcampCategoryType category = BootcampCategoryType.fromKoreanName(categoryName);

		boolean hasCost = !(detail.bootcampCourseName().equals("K-디지털트레이닝") || dto.cost().equals("0"));

		Bootcamp bootcamp = Bootcamp.of(
			center,
			dto.bootcampName(),
			category,
			Integer.parseInt(dto.bootcampDegree()),
			detail.address1(),
			Integer.parseInt(dto.maxCapacity()),
			hasCost,
			LocalDate.parse(dto.trainingStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
			LocalDate.parse(dto.trainingEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
			dto.bootcampLink()
		);

		bootcampRepository.save(bootcamp);
		log.info("저장 성공: {}", bootcamp.getBootcampName());
	}

	// 중복 데이터 여부 확인
	private boolean isDuplicate(BootcampListResponseDto dto) {
		return bootcampRepository.existsByBootcampNameAndBootcampDegree(
			dto.bootcampName(), Integer.parseInt(dto.bootcampDegree())
		);
	}

	// Bootcamp 리스트 API 호출
	private List<BootcampListResponseDto> fetchBootcampList(String categoryCode, String ncsCode) {
		String url = buildListApiUrl(categoryCode, ncsCode);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		validateResponse(response);
		return parseListResponse(response.getBody());
	}

	// Bootcamp 상세 API 호출
	private BootcampDetailResponseDto fetchBootcampDetail(String id, String degree, String centerId) {
		String url = buildDetailApiUrl(id, degree, centerId);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		validateResponse(response);

		try {
			JsonNode detailNode = objectMapper.readTree(response.getBody()).get("inst_base_info");
			return objectMapper.treeToValue(detailNode, BootcampDetailResponseDto.class);
		} catch (Exception e) {
			throw new BootcampCustomException(DATA_PARSING_ERROR);
		}
	}

	// 응답 JSON 파싱 → BootcampList DTO로 변환
	private List<BootcampListResponseDto> parseListResponse(String body) {
		try {
			JsonNode jsonList = objectMapper.readTree(body).get("srchList");

			if (jsonList == null || jsonList.isEmpty()) {
				throw new BootcampCustomException(API_DATA_IS_EMPTY);
			}

			List<BootcampListResponseDto> list = new ArrayList<>();
			for (JsonNode node : jsonList) {
				list.add(objectMapper.treeToValue(node, BootcampListResponseDto.class));
			}
			return list;
		} catch (Exception e) {
			throw new BootcampCustomException(DATA_PARSING_ERROR);
		}
	}

	// 리스트 API 호출 URL 생성
	private String buildListApiUrl(String categoryCode, String ncsCode) {
		LocalDate today = LocalDate.now();
		LocalDate end = today.plusDays(7);
		return UriComponentsBuilder.newInstance()
			.scheme("https")
			.host(HOST)
			.path(LIST_API_PATH)
			.queryParam("authKey", AUTH_KEY)
			.queryParam("returnType", "JSON")
			.queryParam("outType", 1)
			.queryParam("pageNum", 1)
			.queryParam("pageSize", 100)
			.queryParam("srchTraStDt", today.format(formatter))
			.queryParam("srchTraEndDt", end.format(formatter))
			.queryParam("crseTracseSe", categoryCode)
			.queryParam("srchNcs1", ncsCode)
			.build()
			.toUriString();
	}

	// 상세 API 호출 URL 생성
	private String buildDetailApiUrl(String id, String degree, String centerId) {
		return UriComponentsBuilder.newInstance()
			.scheme("https")
			.host(HOST)
			.path(DETAIL_API_PATH)
			.queryParam("authKey", AUTH_KEY)
			.queryParam("returnType", "JSON")
			.queryParam("outType", 2)
			.queryParam("srchTrprId", id)
			.queryParam("srchTrprDegr", degree)
			.queryParam("srchTorgId", centerId)
			.build()
			.toUriString();
	}

	// API 응답 유효성 검사
	private void validateResponse(ResponseEntity<String> response) {
		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			throw new BootcampCustomException(DATA_FETCH_ERROR);
		}
	}
}
