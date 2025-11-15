package com.example.ordersystem.ordering.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderCreateDto;
import com.example.ordersystem.ordering.dto.ProductDto;
import com.example.ordersystem.ordering.dto.ProductQuantityKafkaUpdateRequestDto;
import com.example.ordersystem.ordering.dto.ProductStockQuantityUpdateRequestDto;
import com.example.ordersystem.ordering.repository.OrderingRepository;

@Service
public class OrderingService {
	private final OrderingRepository orderingRepository;
	private final RestTemplate restTemplate;
	private final ProductFeign productFeign;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public OrderingService(OrderingRepository orderingRepository,
						   RestTemplate restTemplate,
						   ProductFeign productFeign,
						   KafkaTemplate<String, Object> kafkaTemplate) {
		this.orderingRepository = orderingRepository;
		this.restTemplate = restTemplate;
		this.productFeign = productFeign;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Transactional
	public Ordering orderCreate(OrderCreateDto orderDto, String userId) {

		int quantity = orderDto.getProductCount();

		String productGetUrl = "http://product-service/product/" + orderDto.getProductId();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("X-USER-ID", userId);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

		ResponseEntity<ProductDto> productResponse =
			this.restTemplate.exchange(productGetUrl, HttpMethod.GET, httpEntity, ProductDto.class);

		ProductDto product = productResponse.getBody();

		if (product.stockQuantity() < quantity) {
			throw new IllegalArgumentException("재고 부족");
		} else {
			String patchProductStockQuantityUrl =
				"http://product-service/product/" + orderDto.getProductId() + "/stock-quantity";
			HttpEntity<ProductStockQuantityUpdateRequestDto> updateEntity =
				new HttpEntity<>(ProductStockQuantityUpdateRequestDto.from(orderDto.getProductCount()), httpHeaders);

			this.restTemplate.exchange(patchProductStockQuantityUrl, HttpMethod.PATCH, updateEntity, ProductDto.class);
		}

		Ordering ordering = Ordering.builder()
			.memberId(Long.parseLong(userId))
			.productId(orderDto.getProductId())
			.quantity(orderDto.getProductCount())
			.build();

		return this.orderingRepository.save(ordering);
	}

	@Transactional
	public Ordering orderFeignCreate(OrderCreateDto orderDto, String userId) {

		int quantity = orderDto.getProductCount();
		Long productId = orderDto.getProductId();

		ProductDto product = this.productFeign.getProductById(productId, userId);

		if (product.stockQuantity() < quantity) {
			throw new IllegalArgumentException("재고 부족");
		} else {
			this.productFeign.updateProductStockQuantity(productId,
														 ProductStockQuantityUpdateRequestDto
															 .from(orderDto.getProductCount()));
		}

		Ordering ordering = Ordering.builder()
			.memberId(Long.parseLong(userId))
			.productId(productId)
			.quantity(orderDto.getProductCount())
			.build();

		return this.orderingRepository.save(ordering);
	}

	@Transactional
	public Ordering orderFeignKafkaCreate(OrderCreateDto orderDto, String userId) {

		int quantity = orderDto.getProductCount();
		Long productId = orderDto.getProductId();

		ProductDto product = this.productFeign.getProductById(productId, userId);

		if (product.stockQuantity() < quantity) {
			throw new IllegalArgumentException("재고 부족");
		} else {
			this.kafkaTemplate.send("update-stock-quantity-topic",
									ProductQuantityKafkaUpdateRequestDto.from(orderDto));
		}

		Ordering ordering = Ordering.builder()
			.memberId(Long.parseLong(userId))
			.productId(productId)
			.quantity(orderDto.getProductCount())
			.build();

		return this.orderingRepository.save(ordering);
	}

}
