package com.icandoit.boottalk.coffeeChat.repository;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatListDto;
import com.icandoit.boottalk.coffeeChat.entity.QCoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.UserType;
import com.icandoit.boottalk.user.domain.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CoffeeChatQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<CoffeeChatListDto> getFilteredCoffeeChatResults(
        @Nullable JobType jobType,
        @Nullable UserType userType,
        Pageable pageable
    ) {
        QCoffeeChatInfo coffeeChatInfo = QCoffeeChatInfo.coffeeChatInfo;
        QUser user = QUser.user;

        List<CoffeeChatListDto> content = queryFactory
            .select(Projections.constructor(
                CoffeeChatListDto.class,
                coffeeChatInfo.coffeeChatInfoId,
                user.userId,
                user.name, // todo: user 테이블 name 테이블 변경 시 userName으로 변경
                coffeeChatInfo.userType,
                coffeeChatInfo.jobType,
                coffeeChatInfo.introduction
            ))
            .from(coffeeChatInfo)
            .join(coffeeChatInfo.user, user)
            .where(
                jobTypeEq(jobType),
                userTypeEq(userType)
            )
            .orderBy(coffeeChatInfo.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
            .select(coffeeChatInfo.count())
            .from(coffeeChatInfo)
            .where(
                jobTypeEq(jobType),
                userTypeEq(userType)
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression jobTypeEq(JobType jobType) {
        return jobType != null ? QCoffeeChatInfo.coffeeChatInfo.jobType.eq(jobType) : null;
    }

    private BooleanExpression userTypeEq(UserType userType) {
        return userType != null ? QCoffeeChatInfo.coffeeChatInfo.userType.eq(userType) : null;
    }
}
