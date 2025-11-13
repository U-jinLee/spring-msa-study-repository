package com.example.ordersystem.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dto.ProductRegisterDto;
import com.example.ordersystem.product.dto.ProductResponseDto;
import com.example.ordersystem.product.dto.ProductStockQuantityUpdateRequestDto;
import com.example.ordersystem.product.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	@Transactional
	public Product productCreate(ProductRegisterDto dto, String userId) {
		return productRepository.save(dto.toEntity(userId));
	}

	@Transactional(readOnly = true)
	public ProductResponseDto getProduct(Long id) {
		Product product = this.productRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("product is not found"));

		return ProductResponseDto.from(product);
	}

	@Transactional
	public ProductResponseDto updateStockQuantity(Long id, ProductStockQuantityUpdateRequestDto request) {
		Product product = this.productRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("product is not found"));

		product.reduceStockQuantity(request.stockQuantity());

		return ProductResponseDto.from(this.productRepository.save(product));
	}
}
