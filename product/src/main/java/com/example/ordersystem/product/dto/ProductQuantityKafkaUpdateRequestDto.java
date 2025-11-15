package com.example.ordersystem.product.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ProductQuantityKafkaUpdateRequestDto(Long productId, Integer stockQuantity) {
}
