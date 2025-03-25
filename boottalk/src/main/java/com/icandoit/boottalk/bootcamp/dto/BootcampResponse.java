package com.icandoit.boottalk.bootcamp.dto;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.CategoryType;
import com.icandoit.boottalk.dto.BaseResponse;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

public class BootcampResponse {

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = true)
	public static class Response extends BaseResponse {

		private Object data;

		public Response(String message, Object data) {
			super(message);
			this.data = data;
		}
	}

	@Data
	public static class BootcampResponseDto {

		private Long id;
		private String tcName;
		private String btName;
		private String region;
		private Boolean cost;
		private String link;
		private String category;
		private int degree;
		private int capacity;
		private LocalDate startDate;
		private LocalDate endDate;

		public BootcampResponseDto() {
		}

		public BootcampResponseDto(Bootcamp bootcamp) {
			CategoryType categoryType = bootcamp.getCategory();

			this.id = bootcamp.getId();
			this.tcName = bootcamp.getTrainingCenter().getName();
			this.btName = bootcamp.getName();
			this.region = bootcamp.getRegion();
			this.cost = bootcamp.isCost();
			this.link = bootcamp.getLink();
			this.category = categoryType.getKoreanNameByEnglishName(categoryType.name());
			this.degree = bootcamp.getDegree();
			this.capacity = bootcamp.getCapacity();
			this.startDate = bootcamp.getStartDate();
			this.endDate = bootcamp.getEndDate();
		}
	}
}
