package com.example.ordersystem.product.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dto.ProductQuantityKafkaUpdateRequestDto;
import com.example.ordersystem.product.dto.ProductRegisterDto;
import com.example.ordersystem.product.dto.ProductResponseDto;
import com.example.ordersystem.product.dto.ProductStockQuantityUpdateRequestDto;
import com.example.ordersystem.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	@KafkaListener(topics = "update-stock-quantity-topic", containerFactory = "kafkaListener")
	public void stockConsumer(String message) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			ProductQuantityKafkaUpdateRequestDto request =
				objectMapper.readValue(message, ProductQuantityKafkaUpdateRequestDto.class);

			this.updateStockQuantityKafka(request);
		} catch (JsonProcessingException e) {
			log.error("Failed to decrease stock", e);
			// 주문 실패시 주문 취소 보상 트랜잭션 추가 필요 ->(feign client)
			e.printStackTrace();
		}
	}

	public void updateStockQuantityKafka(ProductQuantityKafkaUpdateRequestDto requestDto) {
		Product product = this.productRepository.findById(requestDto.productId())
			.orElseThrow(() -> new EntityNotFoundException("product is not found"));

		product.reduceStockQuantity(requestDto.stockQuantity());

		ProductResponseDto.from(this.productRepository.save(product));
	}
}
