package com.icandoit.boottalk.bootcamp.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BootcampCreateRequest {

	private String tcName;
	private String btName;
	private String btCategory;
	private int btDegree;
	private String btRegion;
	private int btCapacity;
	private Boolean btCost;
	private LocalDate btStartDate;
	private LocalDate btEndDate;
	private String btLink;
}
