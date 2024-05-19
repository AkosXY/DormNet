package com.dormnet.reservationservice;

import com.dormnet.reservationservice.mock.ResourceClientStub;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.testcontainers.containers.MySQLContainer;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class ReservationServiceApplicationTests {

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
	void testPlaceReservation_ResourceUnavailable() {
		long resourceId = -1;
		boolean isAvailable = false;
		boolean makeUnavailableSuccess = false;


		ResourceClientStub.stubIsAvailable(resourceId, isAvailable);
		ResourceClientStub.stubMakeUnavailable(resourceId, makeUnavailableSuccess);

		String reservationJson = "{\"resourceId\": " + resourceId + ", \"email\": \"test@example.com\", \"startDate\": \"2024-06-01\", \"stopDate\": \"2024-06-02\"}";

		given()
				.contentType(ContentType.JSON)
				.body(reservationJson)
				.when()
				.post("/api/reservation/reserve")
				.then()
				.statusCode(201)
				.body(containsString("Resource with ID " + resourceId + " is not available. Reservation not placed."));
	}

	@Test
	void testPlaceReservation_ResourceAvailable() {
		long resourceId = 1;
		boolean isAvailable = true;
		boolean makeUnavailableSuccess = true;

		ResourceClientStub.stubIsAvailable(resourceId, isAvailable);
		ResourceClientStub.stubMakeUnavailable(resourceId, makeUnavailableSuccess);

		String reservationJson = "{\"resourceId\": " + resourceId + ", \"email\": \"test@example.com\", \"startDate\": \"2024-06-01\", \"stopDate\": \"2024-06-02\"}";

		given()
				.contentType(ContentType.JSON)
				.body(reservationJson)
				.when()
				.post("/api/reservation/reserve")
				.then()
				.statusCode(201)
				.body(containsString("Reservation successfully placed. Reservation number"));
	}




}
