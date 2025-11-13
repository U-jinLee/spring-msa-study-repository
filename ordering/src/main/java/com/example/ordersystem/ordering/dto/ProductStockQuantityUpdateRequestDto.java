package com.example.ordersystem.ordering.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ProductStockQuantityUpdateRequestDto(Integer stockQuantity) {

	public static ProductStockQuantityUpdateRequestDto from(Integer stockQuantity) {
		return ProductStockQuantityUpdateRequestDto.builder()
			.stockQuantity(stockQuantity)
			.build();
	}

}
