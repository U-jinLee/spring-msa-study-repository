package com.example.ordersystem.ordering.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ProductDto(long id, String name, int price, int stockQuantity, long memberId) {
}
