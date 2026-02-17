package com.appsdeveloperblog.ws.api.ApiGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;


@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = "com.appsdeveloperblog")
@PropertySource("classpath:gateway-application.properties")
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
