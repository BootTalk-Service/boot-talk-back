package com.icandoit.boottalk.bootcamp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.entity.QBootcamp;
import com.icandoit.boottalk.bootcamp.entity.QCourse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BootcampQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<Bootcamp> searchBootcampEntities(
		@Nullable String region,
		@Nullable BootcampCategoryType category,
		@Nullable Integer minRating,
		@Nullable Integer duration,
		String keyword,
		String sort,
		Pageable pageable
	) {
		QBootcamp bootcamp = QBootcamp.bootcamp;
		QCourse course = QCourse.course;

		// 평균 평점 계산: 리뷰 수 0이면 0.0, 아니면 (총점 / 리뷰 수)
		NumberExpression<Double> averageRatingExpr = new CaseBuilder()
			.when(course.reviewCount.eq(0))
			.then(0.0)
			.otherwise(course.totalScore.doubleValue().divide(course.reviewCount.doubleValue()));

		// 부트캠프 주차 수 계산 (start ~ end)
		NumberExpression<Integer> durationInWeeks = Expressions.numberTemplate(
			Integer.class,
			"datediff({0}, {1}) / 7",
			bootcamp.bootcampEndDate,
			bootcamp.bootcampStartDate
		);

		// 실제 페이지 콘텐츠 조회
		List<Bootcamp> content = queryFactory
			.selectFrom(bootcamp)
			.join(bootcamp.course, course).fetchJoin()
			.join(bootcamp.trainingCenter).fetchJoin()
			.where(
				eqRegion(region),                   // 지역 필터
				eqCategory(category),               // 카테고리 필터
				betweenRating(minRating, averageRatingExpr),  // 평점대 필터
				durationFilter(duration, durationInWeeks),     // 기간 필터
				containsKeyword(keyword)            // 키워드 검색 (이름 or 소개글)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(getSortOrder(sort, averageRatingExpr, course, bootcamp)) // 정렬 기준
			.fetch();

		// 총 개수 카운트 쿼리
		JPAQuery<Long> countQuery = queryFactory
			.select(bootcamp.count())
			.from(bootcamp)
			.join(bootcamp.course, course)
			.where(
				eqRegion(region),
				eqCategory(category),
				betweenRating(minRating, averageRatingExpr),
				durationFilter(duration, durationInWeeks),
				containsKeyword(keyword)
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	// 지역 필터 (부분 일치)
	private BooleanExpression eqRegion(String region) {
		return region != null ? QBootcamp.bootcamp.bootcampRegion.contains(region) : null;
	}

	// 카테고리 필터
	private BooleanExpression eqCategory(BootcampCategoryType category) {
		return category != null ? QBootcamp.bootcamp.bootcampCategoryType.eq(category) : null;
	}

	// '3점대' 조건 처리: 3 <= 평점 < 4
	private BooleanExpression betweenRating(Integer minRating, NumberExpression<Double> avgExpr) {
		if (minRating == null) return null;
		return avgExpr.goe(minRating).and(avgExpr.lt(minRating + 1));
	}

	// 주차 필터
	private BooleanExpression durationFilter(Integer durationType, NumberExpression<Integer> weeksExpr) {
		if (durationType == null) return null;
		return switch (durationType) {
			case 1 -> weeksExpr.lt(4);          // 4주 미만
			case 2 -> weeksExpr.between(4, 12); // 4~12주
			case 3 -> weeksExpr.gt(12);         // 12주 초과
			default -> null;
		};
	}

	// 키워드 검색 (이름)
	private BooleanExpression containsKeyword(String keyword) {
		if (keyword == null || keyword.isBlank()) return null;
		return QBootcamp.bootcamp.bootcampName.containsIgnoreCase(keyword);
	}

	// 정렬 기준 처리
	private OrderSpecifier<?> getSortOrder(String sort, NumberExpression<Double> averageRatingExpr, QCourse course, QBootcamp bootcamp) {
		String safeSort = (sort == null || sort.isBlank()) ? "latest" : sort;
		return switch (safeSort) {
			case "rating" -> averageRatingExpr.desc();         // 평점 높은 순
			case "popular" -> course.reviewCount.desc();       // 리뷰 많은 순
			default -> bootcamp.createdAt.desc();              // 최신순 (기본값)
		};
	}
}
