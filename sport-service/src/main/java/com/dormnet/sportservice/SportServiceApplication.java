package com.dormnet.sportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dormnet.sportservice", "com.dormnet.commonsecurity"})
public class SportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportServiceApplication.class, args);
	}

}
