package com.example.ordersystem.ordering.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.ordersystem.ordering.dto.ProductDto;
import com.example.ordersystem.ordering.dto.ProductStockQuantityUpdateRequestDto;

// Eureka에 등록된 호출할 서비스의 이름
@FeignClient(name = "product-service")
public interface ProductFeign {

	@GetMapping("/product/{productId}")
	ProductDto getProductById(@PathVariable Long productId, @RequestHeader("X-USER-ID") String userId);

	@PutMapping("/product/{productId}/stock-quantity")
	ProductDto updateProductStockQuantity(@PathVariable Long productId,
										  @RequestBody ProductStockQuantityUpdateRequestDto requestDto);

}
