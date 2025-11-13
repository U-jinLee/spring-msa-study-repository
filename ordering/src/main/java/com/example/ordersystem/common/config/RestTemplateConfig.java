package com.example.ordersystem.common.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	@LoadBalanced // Eureka에 등록된 서비스명을 통해 내부서비스 호출
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		// PATCH 메서드 사용을 위해 HttpComponentsClientHttpRequestFactory 설정
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		return restTemplate;
	}

}
