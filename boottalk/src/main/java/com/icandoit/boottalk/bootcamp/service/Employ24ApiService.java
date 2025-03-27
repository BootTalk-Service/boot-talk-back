package com.icandoit.boottalk.bootcamp.service;

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
import com.icandoit.boottalk.bootcamp.dto.BootcampListResponseDto;
import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
import com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Employ24ApiService {

	private static final String LIST_API_URL = "https://www.work24.go.kr/cm/openApi/call/hr/callOpenApiSvcInfo310L01.do";
	private static final String DETAIL_API_URL = "https://www.work24.go.kr/cm/openApi/call/hr/callOpenApiSvcInfo310L02.do";
	private static final String AUTH_KEY = "646d5bd7-af0c-42b1-94db-fd6d2c6908f7";
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate;

	LocalDate today = LocalDate.now().minusMonths(12);
	LocalDate endDate = today.plusMonths(6);

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	String todayString = today.format(formatter);
	String endDateString = endDate.format(formatter);

	public List<BootcampListResponseDto> fetchBootcampData(String creseTraceseSe, String srchNcs1) {

		String url = UriComponentsBuilder.newInstance()
			.scheme("https")
			.host("www.work24.go.kr")
			.path("/cm/openApi/call/hr/callOpenApiSvcInfo310L01.do")
			.queryParam("authKey", AUTH_KEY)
			.queryParam("returnType", "JSON")
			.queryParam("outType", 1)
			.queryParam("pageNum", 1)
			.queryParam("pageSize", 100)
			.queryParam("srchTraStDt", todayString)
			.queryParam("srchTraEndDt", endDateString)
			.queryParam("crseTracseSe", creseTraceseSe)
			.queryParam("srchNcs1", srchNcs1)
			.build()
			.toUriString();

		System.out.println("print URL : " + url);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				String.class
			);

			if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
				throw new BootcampCustomException(BootcampErrorCode.DATA_FETCH_ERROR);
			}

			JsonNode rootNode = objectMapper.readTree(response.getBody());
			JsonNode srchListNode = rootNode.get("srchList");
			System.out.println("get srchList : " + rootNode.get("srchList"));

			if(srchListNode.isEmpty()) {
				throw new BootcampCustomException(BootcampErrorCode.API_DATA_IS_EMPTY);
			}

			List<BootcampListResponseDto> bootcampDataList = new ArrayList<>();

			for(JsonNode node : srchListNode) {
				try {
					BootcampListResponseDto bootcampDto = objectMapper.treeToValue(node, BootcampListResponseDto.class);
					bootcampDataList.add(bootcampDto);
					System.out.println("파싱 성공 : " + bootcampDto.getBootcampName());
				} catch (Exception e) {
					System.out.println("파싱 실패 : " + e.getMessage());
				}
			}

			bootcampDataList.forEach(dto -> System.out.println(dto.getBootcampName()));
			return bootcampDataList;

		} catch (Exception e) {
			throw new BootcampCustomException(BootcampErrorCode.DATA_FETCH_ERROR);
		}
	}
}
