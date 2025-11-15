package com.example.ordersystem.ordering.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ProductQuantityKafkaUpdateRequestDto(Long productId, Integer stockQuantity) {

	public static ProductQuantityKafkaUpdateRequestDto from(OrderCreateDto orderDto) {
		return ProductQuantityKafkaUpdateRequestDto.builder()
			.productId(orderDto.getProductId())
			.stockQuantity(orderDto.getProductCount())
			.build();
	}

}
