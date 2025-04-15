package com.icandoit.boottalk.point_history.domain.entity;

import com.icandoit.boottalk.libs.entity.BaseEntity;
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

	@Column(nullable = false)
	private int currentPoint;

	@Column(nullable = false)
	private int changedPoint;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(20)", nullable = false)
	private PointType pointType;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(50)", nullable = false)
	private EventType eventType;

}
