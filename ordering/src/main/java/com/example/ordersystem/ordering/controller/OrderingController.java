package com.example.ordersystem.ordering.controller;

import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderCreateDto;
import com.example.ordersystem.ordering.service.OrderingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordering")
public class OrderingController {
	private final OrderingService orderingService;

	public OrderingController(OrderingService orderingService) {
		this.orderingService = orderingService;
	}

	@PostMapping("/create")
	public ResponseEntity<Long> orderCreate(@RequestBody OrderCreateDto dtos,
											@RequestHeader("X-USER-ID") String userId) {

		Ordering result = orderingService.orderFeignKafkaCreate(dtos, userId);

		return ResponseEntity.status(HttpStatus.CREATED).body(result.getId());
	}

}
