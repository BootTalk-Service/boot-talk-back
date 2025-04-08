package com.icandoit.boottalk.bootcamp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.bootcamp.dto.CourseAutocompleteDto;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

	private final CourseRepository courseRepository;

	@GetMapping("/autocomplete")
	public ResponseEntity<List<CourseAutocompleteDto>> autocompleteCourses(
		@RequestParam("query") String query
	) {
		List<Course> courses = courseRepository.findByCourseNameContainingIgnoreCase(query);
		List<CourseAutocompleteDto> result = courses.stream()
			.map(course -> new CourseAutocompleteDto(course.getCourseId(), course.getCourseName()))
			.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}
}
