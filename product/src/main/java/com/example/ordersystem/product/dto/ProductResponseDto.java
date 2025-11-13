package com.example.ordersystem.product.dto;

import com.example.ordersystem.product.domain.Product;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ProductResponseDto(long id, String name, int price, int stockQuantity, long memberId) {

	public static ProductResponseDto from(Product product) {
		return ProductResponseDto.builder()
			.id(product.getId())
			.name(product.getName())
			.price(product.getPrice())
			.stockQuantity(product.getStockQuantity())
			.memberId(product.getMemberId())
			.build();
	}

}
