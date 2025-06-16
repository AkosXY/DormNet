package com.dormnet.reservationservice;

import com.dormnet.reservationservice.mock.ResourceClientStub;
import com.dormnet.reservationservice.mock.TestJwtDecoderConfig;
import com.dormnet.reservationservice.model.Reservation;
import com.dormnet.reservationservice.model.ReservationRequest;
import com.dormnet.reservationservice.repository.ReservationRepository;
import com.dormnet.reservationservice.service.ReservationService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@Import(TestJwtDecoderConfig.class)
class ReservationServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.35");

	@Autowired
	ReservationService reservationService;

	@Autowired
	ReservationRepository reservationRepository;

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


	@Test
	void testGetMyActiveReservations() {
		Long resourceId = 96L;
		ResourceClientStub.stubIsAvailable(resourceId, true);

		Date start = new Date(System.currentTimeMillis() - 1000 * 60 * 60);
		Date end = new Date(System.currentTimeMillis() + 1000 * 60 * 60);

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
				.statusCode(201);

		RestAssured.given()
				.auth().oauth2(token)
				.when()
				.get("/api/reservation/activeReservations")
				.then()
				.log().all()
				.statusCode(200)
				.body("size()", greaterThan(0));
	}


	@Test
	void testGetAllActiveReservations_adminAccess() {
		Long resourceId = 96L;
		ResourceClientStub.stubIsAvailable(resourceId, true);

		// Create a reservation with admin token (or simulate it)
		ReservationRequest req = new ReservationRequest(
				null, null, resourceId, "dryer", null,
				new Date(System.currentTimeMillis() + 3600_000), // 1 hour from now
				new Date(System.currentTimeMillis() + 7200_000)  // 2 hours from now
		);

		RestAssured.given()
				.auth().oauth2("admin-token") // assume this user has the role 'admin'
				.contentType("application/json")
				.body(req)
				.when()
				.post("/api/reservation/reserve")
				.then()
				.statusCode(201);

		// Check that the active reservation appears in the admin-only endpoint
		RestAssured.given()
				.auth().oauth2("admin-token")
				.when()
				.get("/api/reservation/allActive")
				.then()
				.statusCode(200)
				.body("size()", greaterThan(0));
	}


	@Test
	void testDeleteReservationsByResourceId_directly() {
		Long resourceIdToDelete = 999L;
		Long resourceIdOther    = 1000L;

		reservationRepository.save(new Reservation(null, UUID.randomUUID().toString(), resourceIdToDelete, "A", "email@test", new Date(), new Date()));
		reservationRepository.save(new Reservation(null, UUID.randomUUID().toString(), resourceIdOther, "B", "email@test", new Date(), new Date()));

		assertThat(reservationRepository.findByResourceId(resourceIdToDelete)).isNotEmpty();
		assertThat(reservationRepository.findByResourceId(resourceIdOther)).isNotEmpty();

		reservationService.deleteReservationsByResourceId(resourceIdToDelete);

		assertThat(reservationRepository.findByResourceId(resourceIdToDelete)).isEmpty();
		assertThat(reservationRepository.findByResourceId(resourceIdOther)).isNotEmpty();
	}

	@Test
	void dropReservation_endpoint_nonExistentId() {
		Long nonExistentId = 999999L;

		RestAssured.given()
				.auth().oauth2(token)
				.when()
				.post("/api/reservation/drop?id=" + nonExistentId)
				.then()
				.statusCode(404)
				.body(containsString("Reservation with ID " + nonExistentId + " not found"));
	}


	@Test
	void testGetAvailableTimeSlots_restEndpoint_WithReservations() {
		Long resourceId = 201L;
		LocalDate date = LocalDate.of(2025, 7, 1);

		Reservation res = new Reservation(
				null, UUID.randomUUID().toString(), resourceId, "test", "user@test",
				Timestamp.valueOf(date.atTime(10, 0)),
				Timestamp.valueOf(date.atTime(11, 0))
		);
		reservationRepository.save(res);

		RestAssured.given()
				.auth().oauth2(token)
				.queryParam("resourceId", resourceId)
				.queryParam("date", date.toString())
				.queryParam("durationMinutes", 30)
				.when()
				.get("/api/reservation/availability")
				.then()
				.statusCode(200)
				.body("", not(hasItem("10:00")))
				.body("", not(hasItem("10:15")))
				.body("", not(hasItem("10:30")));
	}
}
