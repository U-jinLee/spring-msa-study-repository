package com.example.ordersystem.product.domain;

import com.example.ordersystem.common.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Integer price;

	private Integer stockQuantity;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Builder
	public Product(String name, int price, int stockQuantity, long memberId) {
		this.name = name;
		this.price = price;
		this.stockQuantity = stockQuantity;
		this.memberId = memberId;
	}

	public void reduceStockQuantity(int stockQuantity) {
		this.stockQuantity -= stockQuantity;
	}

}
