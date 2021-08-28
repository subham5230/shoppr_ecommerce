package com.shoppr.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.shoppr.common.entity", "com.shoppr.admin.user"})
public class ShopprBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopprBackendApplication.class, args);
	}

}
