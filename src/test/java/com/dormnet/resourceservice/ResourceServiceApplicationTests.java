package com.dormnet.resourceservice;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResourceServiceApplicationTests {


	@ServiceConnection
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");


	@LocalServerPort
	private Integer port;

	@BeforeAll
	public static void setUpClass() {
		mySQLContainer.start();
	}

	@AfterAll
	public static void tearDownClass() {
		mySQLContainer.stop();
	}

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void testCreateResource() {
		String resourceJson = "{\"name\": \"Test Resource\", \"status\": \"Active\", \"available\": true}";

		given()
				.contentType(ContentType.JSON)
				.body(resourceJson)
				.when()
				.post("/api/resource/add")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Test Resource"))
				.body("status", equalTo("Active"))
				.body("available", equalTo(true));
	}


}
