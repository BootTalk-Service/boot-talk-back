package com.icandoit.boottalk.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Page<Review> findByUser_UserId(Long userId, Pageable pageable);

	boolean existsByCourse_TrainingProgramIdAndUser_UserId(String trainingProgramId, Long userId);

	// 해당 Course 에 속한 리뷰들을 페이징 처리하여 반환
	Page<Review> findByCourse(Course course, Pageable pageable);

	@Query("""
    SELECT r FROM Review r
    WHERE r.course IN (
        SELECT b.course FROM Bootcamp b
        WHERE b.bootcampCategoryType = :categoryType
    )
""")
	Page<Review> findByBootcampCategory(@Param("categoryType") BootcampCategoryType categoryType, Pageable pageable);
}
