package com.icandoit.boottalk.point_history.domain.entity;

import com.icandoit.boottalk.common.entity.BaseEntity;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.domain.type.PointType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pointHistoryId;

	@Column(nullable = false)
	private long userId;

	private int currentPoint;
	private int changedPoint;

	@Enumerated(EnumType.STRING)
	private PointType pointType;

	@Enumerated(EnumType.STRING)
	private EventType eventType;

}
