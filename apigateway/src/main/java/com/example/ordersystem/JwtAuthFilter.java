package com.example.ordersystem;

import java.security.Key;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

/**
 * Netty 기반의 Spring WebFlux 환경에서 JWT 인증을 처리하는 글로벌 필터 클래스입니다.
 */
@Component
public class JwtAuthFilter implements GlobalFilter {

	@Value("${jwt.secret-key}")
	private String secretKey;

	private Key key;

	@PostConstruct
	public void init() {
		byte[] decodeKeyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(decodeKeyBytes);
	}

	private static final List<String> ALLOWED_PATHS = List.of(
		"/member/sign-up",
		"/member/sign-in",
		"/member/refresh-token",
		"/products"
	);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		String path = exchange.getRequest().getURI().getRawPath();

		if (ALLOWED_PATHS.contains(path))
			return chain.filter(exchange);

		try {
			if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
				throw new IllegalArgumentException("Invalid Authorization header");
			}

			String token = bearerToken.substring(7);

			Claims claims = Jwts.parserBuilder()
				.setSigningKey(this.key)
				.build()
				.parseClaimsJws(token)
				.getBody();

			String userId = claims.getSubject();
			String role = claims.get("role", String.class);

			// X-USER-ID and X-USER-ROLE headers 추가
			ServerWebExchange modifiedExchange = exchange.mutate()
				.request(builder ->
							 builder
								 .header("X-USER-ID", userId)
								 .header("X-USER-ROLE", role))
				.build();

			return chain.filter(modifiedExchange);
		} catch (IllegalArgumentException | MalformedJwtException | ExpiredJwtException | SignatureException |
				 UnsupportedJwtException e) {
			e.printStackTrace();
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

			return exchange.getResponse().setComplete();
		}
	}
}
