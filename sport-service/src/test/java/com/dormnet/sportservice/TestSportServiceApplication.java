package com.dormnet.sportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class TestSportServiceApplication {

	@Bean
	@ServiceConnection
	MongoDBContainer mongoDbContainer() {
		return new MongoDBContainer(DockerImageName.parse("mongo:latest"))
				.waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));
	}
	public static void main(String[] args) {
		SpringApplication.from(SportServiceApplication::main).with(TestSportServiceApplication.class).run(args);
	}

}
