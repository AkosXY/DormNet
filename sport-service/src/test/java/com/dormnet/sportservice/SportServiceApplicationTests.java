package com.dormnet.sportservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SportServiceApplicationTests {

	@ServiceConnection
	public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@LocalServerPort
	private Integer port;

	@BeforeAll
	public static void setUpClass() {
		mongoDBContainer.start();
	}

	@AfterAll
	public static void tearDownClass() {
		mongoDBContainer.stop();
	}

	@BeforeEach
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}


	@Test
	void testCreateEvent() {
		String sportEventJson = "{\"name\": \"Test Name\", \"date\": \"2024-05-15\"}";

		given()
				.contentType(ContentType.JSON)
				.body(sportEventJson)
				.when()
				.post("/api/sport")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Test Name"))
				.body("date", equalTo("2024-05-15"));

	}


}
