package com.icandoit.boottalk.bootcamp.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;

@RestController
@RequestMapping("/api/job-roles")
public class CategoryController {

	@GetMapping
	public ResponseEntity<List<String>> getAllCategories() {
		List<String> categories = Arrays.stream(BootcampCategoryType.values())
			.map(BootcampCategoryType::getKoreanName)
			.toList();

		return ResponseEntity.ok(categories);
	}
}
