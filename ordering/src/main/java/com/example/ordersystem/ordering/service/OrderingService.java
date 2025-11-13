package com.example.ordersystem.ordering.service;

import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderCreateDto;
import com.example.ordersystem.ordering.repository.OrderingRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderingService {
	private final OrderingRepository orderingRepository;
	private final MemberRepository memberRepository;
	private final ProductRepository productRepository;

	public OrderingService(OrderingRepository orderingRepository,
						   MemberRepository memberRepository,
						   ProductRepository productRepository) {
		this.orderingRepository = orderingRepository;
		this.memberRepository = memberRepository;
		this.productRepository = productRepository;
	}

	@Transactional
	public Ordering orderCreate(OrderCreateDto orderDto, String userId) {
		Member member = memberRepository.findById(Long.parseLong(id))
			.orElseThrow(() -> new EntityNotFoundException("member is not found"));

		Product product = productRepository.findById(orderDto.getProductId())
			.orElseThrow(() -> new EntityNotFoundException("product is not found"));

		int quantity = orderDto.getProductCount();

		if (product.getStockQuantity() < quantity) {
			throw new IllegalArgumentException("재고 부족");
		} else {
			product.updateStockQuantity(orderDto.getProductCount());
		}

		Ordering ordering = Ordering.builder()
			.memberId(Long.parseLong(userId))
			.productId(orderDto.getProductId())
			.quantity(orderDto.getProductCount())
			.build();

		return this.orderingRepository.save(ordering);
	}

}
