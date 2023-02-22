package com.shoppingcart.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({ "com.shoppingcart.admin.*", "com.shoppingcart.admin" })
@EnableJpaRepositories(basePackages = { "com.shoppingcart.admin.*" })
@EntityScan({ "com.shoppingcart.admin.*" })
public class ShoppingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingBackendApplication.class, args);
	}

}
