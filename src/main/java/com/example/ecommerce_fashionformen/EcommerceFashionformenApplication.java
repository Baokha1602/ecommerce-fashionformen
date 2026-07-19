package com.example.ecommerce_fashionformen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EcommerceFashionformenApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceFashionformenApplication.class, args);
	}

}
