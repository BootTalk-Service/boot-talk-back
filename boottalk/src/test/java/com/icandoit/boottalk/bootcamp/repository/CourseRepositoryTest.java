package com.icandoit.boottalk.bootcamp.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;

@DataJpaTest
class CourseRepositoryTest {

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private TrainingCenterRepository trainingCenterRepository;

	@Test
	@DisplayName("Course 저장 및 trainingProgramId로 조회")
	void saveAndFindByTrainingProgramId() {
	    //given
		TrainingCenter center = trainingCenterRepository.save(
			TrainingCenter.of(
				"test center",
				"test email",
				"test address",
				"test url",
				"test phoneNumber"
			)
		);

		Course course = courseRepository.save(
			Course.builder().build();
		)
	    //when
	    //then
	}

}