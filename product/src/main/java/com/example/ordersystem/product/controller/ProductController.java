package com.example.ordersystem.product.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dto.ProductRegisterDto;
import com.example.ordersystem.product.dto.ProductResponseDto;
import com.example.ordersystem.product.dto.ProductStockQuantityUpdateRequestDto;
import com.example.ordersystem.product.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {
	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/create")
	public ResponseEntity<Object> productCreate(@RequestBody ProductRegisterDto dto,
												@RequestHeader("X-USER-ID") String userId) {
		Product product = productService.productCreate(dto, userId);
		return ResponseEntity.created(URI.create(String.valueOf(product.getId()))).body(product.getId());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
		ProductResponseDto result = this.productService.getProduct(id);
		return ResponseEntity.ok(result);
	}

	@PatchMapping("/{id}/stock-quantity")
	public ResponseEntity<ProductResponseDto> updateStockQuantity(@PathVariable Long id, @RequestBody
	ProductStockQuantityUpdateRequestDto request) {
		return ResponseEntity.ok(this.productService.updateStockQuantity(id, request));
	}
}
