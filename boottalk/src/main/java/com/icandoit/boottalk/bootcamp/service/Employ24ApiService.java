package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.bootcamp.dto.BootcampEmploy24Response.*;
import static com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Employ24ApiService {

	private static final String HOST = "www.work24.go.kr";
	private static final String LIST_API_PATH = "/cm/openApi/call/hr/callOpenApiSvcInfo310L01.do";
	private static final String DETAIL_API_PATH = "/cm/openApi/call/hr/callOpenApiSvcInfo310L02.do";
	private static final String AUTH_KEY = "646d5bd7-af0c-42b1-94db-fd6d2c6908f7";

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate;
	private final TrainingCenterRepository trainingCenterRepository;
	private final BootcampRepository bootcampRepository;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");


	public void saveAllFromEmploy24() {
		List<String> categoryCodes = List.of("C0061", "C0104", "C0105");
		List<String> ncsCodes = List.of("19", "20");

		for(String categoryCode : categoryCodes) {
			for(String ncsCode : ncsCodes) {
				try {
					List<BootcampListResponseDto> list = fetchBootcampList(categoryCode, ncsCode);
					for(BootcampListResponseDto dto : list) {
						saveBootcamp(dto);
					}
				} catch (Exception e) {
					log.warn("데이터 수집 실패: categoryCode={}, ncsCode={}, message={}", categoryCode, ncsCode, e.getMessage());
				}
			}
		}
	}

	private List<BootcampListResponseDto> fetchBootcampList(String categoryCode, String ncsCode) {
		String url = buildListApiUrl(categoryCode, ncsCode);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		validateResponse(response);

		try {
			JsonNode srchList = objectMapper.readTree(response.getBody()).get("srchList");

			if(srchList == null || srchList.isEmpty()) {
				throw new BootcampCustomException(API_DATA_IS_EMPTY);
			}

			List<BootcampListResponseDto> result = new ArrayList<>();

			for (JsonNode node : srchList) {
				result.add(objectMapper.treeToValue(node, BootcampListResponseDto.class));
			}

			return result;
		} catch (Exception e) {
			throw new BootcampCustomException(DATA_PARSING_ERROR);
		}
	}

	private void saveBootcamp(BootcampListResponseDto dto) {
		try {
			BootcampDetailResponseDto detail = fetchBootcampDetail(dto.bootcampId(), dto.bootcampDegree(), dto.trainingCenterId());

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

			BootcampCategoryType category = BootcampCategoryType.fromKoreanName(detail.ncsName());

			boolean hasCost = !(detail.bootcampCourseName().equals("K-디지털트레이닝") || dto.maxCapacity().equals("0"));

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
		}catch(Exception e) {
			log.warn("저장 실패: {}", dto.bootcampName(), e);
		}
	}

	private BootcampDetailResponseDto fetchBootcampDetail(String id, String degree, String centerId) {
		String url = buildDetailApiUrl(id, degree, centerId);
		log.info("Fetch detail from URL: {}", url);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		validateResponse(response);

		try {
			JsonNode detailNode = objectMapper.readTree(response.getBody()).get("inst_base_info");
			return objectMapper.treeToValue(detailNode, BootcampDetailResponseDto.class);
		} catch (Exception e) {
			throw new BootcampCustomException(DATA_PARSING_ERROR);
		}
	}

	private String buildListApiUrl(String categoryCode, String ncsCode) {
		LocalDate today = LocalDate.now();
		LocalDate end = today.plusDays(1);
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

	private void validateResponse(ResponseEntity<String> response) {
		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			throw new BootcampCustomException(DATA_FETCH_ERROR);
		}
	}
}
