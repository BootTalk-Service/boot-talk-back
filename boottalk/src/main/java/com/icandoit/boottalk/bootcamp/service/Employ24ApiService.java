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
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;
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
	private final CourseRepository courseRepository;

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

		TrainingCenter center = saveOrGetTrainingCenter(dto, detail);
		BootcampCategoryType category = BootcampCategoryType.fromKoreanName(categoryName);

		Course course = saveOrGetCourse(dto, center, category);

		Bootcamp bootcamp = createBootcampEntity(center, course, dto, category, detail);

		bootcampRepository.save(bootcamp);
		log.info("저장 성공: {}", bootcamp.getBootcampName());
	}

	// TrainingCenter가 존재하지 않으면 저장, 있으면 조회해서 반환
	private TrainingCenter saveOrGetTrainingCenter(BootcampListResponseDto dto, BootcampDetailResponseDto detail) {
		String address1 = (detail.address1() != null) ? detail.address1() : "";
		String address2 = (detail.address2() != null) ? detail.address2() : "";
		String fullAddress = address1 + " " + address2;

		return trainingCenterRepository.findByTrainingCenterName(dto.trainingCenterName())
			.orElseGet(() -> trainingCenterRepository.save(
				TrainingCenter.of(
					dto.trainingCenterName(),
					detail.trainingCenterEmail(),
					fullAddress,
					detail.trainingCenterUrl(),
					detail.trainingCenterTelephoneNumber()
				)
			));
	}

	// Course 가 존재하지 않으면 저장, 있으면 조회해서 반환
	private Course saveOrGetCourse(BootcampListResponseDto dto, TrainingCenter center, BootcampCategoryType category) {
		return courseRepository.findByTrainingProgramId(dto.bootcampId())
			.orElseGet(() -> courseRepository.save(
				Course.of(dto.bootcampId(), dto.bootcampName(), category, center)
			));
	}

	// Bootcamp Entity 생성
	private Bootcamp createBootcampEntity(
		TrainingCenter center, Course course, BootcampListResponseDto dto,
		BootcampCategoryType category, BootcampDetailResponseDto detail
	) {
		String bootcampRegion = (detail.address1() != null && !detail.address1().isEmpty())
			? detail.address1() : "미입력";

		return Bootcamp.of(
			center,
			course,
			dto.bootcampName(),
			category,
			Integer.parseInt(dto.bootcampDegree()),
			bootcampRegion,
			Integer.parseInt(dto.maxCapacity()),
			!(detail.bootcampCourseName().equals("K-디지털트레이닝") || dto.cost().equals("0")),
			LocalDate.parse(dto.trainingStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
			LocalDate.parse(dto.trainingEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
			dto.bootcampLink()
		);
	}


	// 중복 데이터 여부 확인
	private boolean isDuplicate(BootcampListResponseDto dto) {
		return bootcampRepository.existsByCourse_TrainingProgramIdAndBootcampDegree(
			dto.bootcampId(), Integer.parseInt(dto.bootcampDegree())
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

	// 응답 JSON 파싱 → BootcampList DTO 로 변환
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
