package com.dormnet.accommodationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {"com.dormnet.accommodationservice", "com.dormnet.commonsecurity"})
public class AccommodationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccommodationServiceApplication.class, args);
	}

}
