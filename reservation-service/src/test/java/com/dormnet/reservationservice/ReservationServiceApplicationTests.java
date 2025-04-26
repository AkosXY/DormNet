package com.dormnet.reservationservice;

import com.dormnet.reservationservice.mock.ResourceClientStub;
import com.dormnet.reservationservice.mock.TestJwtDecoderConfig;
import com.dormnet.reservationservice.model.ReservationRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
//@ActiveProfiles("test")
@Import(TestJwtDecoderConfig.class)
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

	private String token = "test-token";

	@Test
	void testReservationSuccess() {
		Long resourceId = 90L;
		ResourceClientStub.stubIsAvailable(resourceId, true);

		ReservationRequest req = new ReservationRequest(
				null, null, resourceId, "washing machine", null,
				new Date(),
				new Date()
		);

		RestAssured.given()
				.auth().oauth2(token)
				.contentType("application/json")
				.body(req)
				.when()
				.post("/api/reservation/reserve")
				.then()
				.statusCode(201)
				.body(containsString("Reservation placed"));
	}

	@Test
	void testResourceNotAvailable() {
		Long resourceId = 91L;
		ResourceClientStub.stubIsAvailable(resourceId, false);

		ReservationRequest req = new ReservationRequest(
				null, null, resourceId, "dryer", null,
				new Date(),
				new Date()
		);

		RestAssured.given()
				.auth().oauth2(token)
				.contentType("application/json")
				.body(req)
				.when()
				.post("/api/reservation/reserve")
				.then()
				.statusCode(201)
				.body(containsString("not available for rental"));
	}

	@Test
	void testReservationOverlap() {
		Long resourceId = 92L;
		ResourceClientStub.stubIsAvailable(resourceId, true);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date start = null;
		Date end = null;
		try {
			start = sdf.parse("2025-04-10T10:30:00");
			end = sdf.parse("2025-04-10T18:30:00");

		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		ReservationRequest req = new ReservationRequest(
				null, null, resourceId, "dryer", null, start, end
		);

		RestAssured.given()
				.auth().oauth2(token)
				.contentType("application/json")
				.body(req)
				.when()
				.post("/api/reservation/reserve")
				.then()
				.log().all()

				.statusCode(201)
				.body(containsString("Reservation placed"));

		RestAssured.given()
				.auth().oauth2(token)
				.contentType("application/json")
				.body(req)
				.when()
				.post("/api/reservation/reserve")
				.then()
				.log().all()

				.statusCode(201)
				.body(containsString("already reserved"));
	}

	@Test
	void testGetMyReservations() {
		Long resourceId = 93L;
		ResourceClientStub.stubIsAvailable(resourceId, true);

		ReservationRequest req = new ReservationRequest(
				null, null, resourceId, "dryer", null,
				new Date(),
				new Date()
		);

		RestAssured.given()
				.auth().oauth2(token)
				.contentType("application/json")
				.body(req)
				.when()
				.post("/api/reservation/reserve")
				.then()
				.statusCode(201);

		RestAssured.given()
				.auth().oauth2(token)
				.when()
				.get("/api/reservation/reservations")
				.then()
				.statusCode(200)
				.body("size()", greaterThan(0));
	}

	@Test
	void testDropReservation() {
		Long resourceId = 94L;
		ResourceClientStub.stubIsAvailable(resourceId, true);

		ReservationRequest req = new ReservationRequest(
				null, null, resourceId, "dryer", null,
				new Date(),
				new Date()
		);

		String response = RestAssured.given()
				.auth().oauth2(token)
				.contentType("application/json")
				.body(req)
				.when()
				.post("/api/reservation/reserve")
				.then()
				.statusCode(201)
				.extract().asString();

		Long reservationId = RestAssured.given()
				.auth().oauth2(token)
				.when()
				.get("/api/reservation/reservations")
				.then()
				.statusCode(200)
				.extract().jsonPath().getLong("[0].id");

		RestAssured.given()
				.auth().oauth2(token)
				.when()
				.post("/api/reservation/drop?id=" + reservationId)
				.then()
				.statusCode(201);

		RestAssured.given()
				.auth().oauth2(token)
				.when()
				.get("/api/reservation/reservations")
				.then()
				.statusCode(200)
				.body("id", not(hasItem(reservationId.intValue())));
	}

	@Test
	void testGetAvailability() {
		Long resourceId = 95L;
		ResourceClientStub.stubIsAvailable(resourceId, true);
		LocalDate date = LocalDate.of(2025, 9, 1);

		RestAssured.given()
				.auth().oauth2(token)
				.queryParam("resourceId", resourceId)
				.queryParam("date", date.toString())
				.queryParam("durationMinutes", 30)
				.when()
				.get("/api/reservation/availability")
				.then()
				.log().all()
				.statusCode(200)
				.body("size()", greaterThan(0));
	}

	@Test
	void testGetAllReservations_adminAccess() {
		RestAssured.given()
				.auth().oauth2("admin-token")
				.when()
				.get("/api/reservation/all")
				.then()
				.statusCode(200);
	}

	@Test
	void testGetAllReservations_userAccess() {
		RestAssured.given()
				.auth().oauth2("user-token")
				.when()
				.get("/api/reservation/all")
				.then()
				.log().all()
				.statusCode(403);
	}



//	@Test
//	void testPlaceReservation_shouldSucceedWithMinimalBody() {
//		Long resourceId = 3L;
//		ResourceClientStub.stubIsAvailable(resourceId, true);
//
//		ReservationRequest req = new ReservationRequest(
//				null,
//				null,
//				resourceId,
//				"washing machine",
//				null,
//				new Date(),
//				new Date()
//		);
//
//		RestAssured
//				.given()
//				.auth().oauth2("test-token")
//				.contentType("application/json")
//				.body(req)
//				.when()
//				.post("/api/reservation/reserve")
//				.then()
//				.statusCode(201)
//				.body(containsString("Reservation placed"));
//	}
//
//



}
