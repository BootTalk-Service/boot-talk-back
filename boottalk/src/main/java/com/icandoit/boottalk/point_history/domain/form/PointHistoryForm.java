package com.icandoit.boottalk.point_history.domain.form;

import com.icandoit.boottalk.point_history.domain.type.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//테스트 용
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryForm {

	private EventType eventType;

	private long userId;

	private int points;
}
