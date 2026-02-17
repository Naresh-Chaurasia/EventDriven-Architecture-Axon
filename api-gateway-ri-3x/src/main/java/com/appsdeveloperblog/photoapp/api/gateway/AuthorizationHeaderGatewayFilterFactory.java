package com.appsdeveloperblog.photoapp.api.gateway;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthorizationHeaderGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationHeaderGatewayFilterFactory.class);

	@Autowired
	Environment env;

	public AuthorizationHeaderGatewayFilterFactory() {
		super(Config.class);
	}


	public static class Config {
		// Put configuration properties here
	}

	@Override
	public GatewayFilter apply(Config config) {
		logger.info("Applying AuthorizationHeaderGatewayFilter");
		return (exchange, chain) -> {

			ServerHttpRequest request = exchange.getRequest();
			logger.debug("Processing request to path: {}", request.getURI().getPath());

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				logger.warn("No authorization header found in request");
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}

			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			logger.debug("Authorization header: {}", authorizationHeader);
			String jwt = authorizationHeader.replace("Bearer", "").trim();

			if (!isJwtValid(jwt)) {
				logger.error("JWT token validation failed");
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			}

			logger.info("JWT token validated successfully");
			return chain.filter(exchange);
		};
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		logger.error("Gateway filter error: {} - {}", err, httpStatus);
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		
		try {
			String errorResponse = String.format(
				"{\"timestamp\":\"%d\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
				System.currentTimeMillis(),
				httpStatus.value(),
				httpStatus.getReasonPhrase(),
				err,
				exchange.getRequest().getURI().getPath()
			);
			
			byte[] bytes = errorResponse.getBytes();
			DataBuffer buffer = response.bufferFactory().wrap(bytes);
			return response.writeWith(Mono.just(buffer));
		} catch (Exception e) {
			logger.error("Error writing error response: {}", e.getMessage());
			return response.setComplete();
		}
	}

	private boolean isJwtValid(String jwt) {
		logger.debug("Validating JWT token");
		boolean returnValue = true;

		String subject = null;

		try {
			// Use the same key format as JWT service - no Base64 encoding
			SecretKey key = Keys.hmacShaKeyFor(env.getProperty("token.secret").getBytes());
			JwtParser parser = Jwts.parser()
					.verifyWith(key)
					.build();
			subject = parser.parseSignedClaims(jwt).getPayload().getSubject();
			logger.debug("JWT token subject extracted: {}", subject);
		} catch (Exception ex) {
			logger.error("JWT token parsing failed: {}", ex.getMessage());
			returnValue = false;
		}

		if (subject == null || subject.isEmpty()) {
			logger.warn("JWT token subject is null or empty");
			returnValue = false;
		}

		logger.info("JWT token validation result: {}", returnValue);
		return returnValue;
	}

}
