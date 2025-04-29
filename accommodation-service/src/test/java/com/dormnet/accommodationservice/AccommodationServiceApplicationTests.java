package com.dormnet.accommodationservice;

import com.dormnet.accommodationservice.modell.Resident;
import com.dormnet.accommodationservice.modell.Room;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import io.restassured.RestAssured;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccommodationServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");

	@LocalServerPort
	private Integer port;

	@BeforeAll
	static void setupClass() { mySQLContainer.start(); }

	@AfterAll
	static void tearDown() { mySQLContainer.stop(); }

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	private String adminToken = "admin-token";
	private String userToken = "user-token";

	@Test
	void anyoneCanListRooms() {
		RestAssured.given()
				.auth().oauth2(userToken)
				.when().get("/api/rooms")
				.then().statusCode(200)
				.body("number", notNullValue());
	}

	@Test
	void adminCanCreateRoom_userForbidden() {
		Room room = new Room();
		room.setNumber("A506");
		room.setCapacity(3);
		room.setNumOfResidents(0);
		room.setResidents(new ArrayList<>());

		Long roomId = RestAssured.given()
				.auth().oauth2(adminToken)
				.contentType("application/json")
				.body(room)
				.when().post("/api/rooms")
				.then().statusCode(201)
				.extract().jsonPath().getLong("id");

		RestAssured.given()
				.auth().oauth2(userToken)
				.contentType("application/json")
				.body(room)
				.when().post("/api/rooms")
				.then().statusCode(403);
	}

	@Test
	void anyoneCanListResidentsAndUnassigned() {
		RestAssured.given()
				.auth().oauth2(userToken)
				.when().get("/api/resident")
				.then().statusCode(200);

		RestAssured.given()
				.auth().oauth2(userToken)
				.when().get("/api/resident/unassigned")
				.then().statusCode(200);
	}

	@Test
	void adminCanCreateResident_userForbidden() {
		Resident resident = new Resident();
		resident.setName("John Doe");
		resident.setUsername("jdoe");
		resident.setEmail("jdoe@example.com");
		resident.setPhone("555-1234");
		resident.setRoom(null);

		Long residentId = RestAssured.given()
				.auth().oauth2(adminToken)
				.contentType("application/json")
				.body(resident)
				.when().post("/api/resident")
				.then().statusCode(201)
				.body("name", equalTo("John Doe"))
				.extract().jsonPath().getLong("id");

		RestAssured.given()
				.auth().oauth2(userToken)
				.contentType("application/json")
				.body(resident)
				.when().post("/api/resident")
				.then().statusCode(403);
	}

	@Test
	void adminCanAssignAndUnassignResidentToRoom() {
		Room room = new Room();
		room.setNumber("A100");
		room.setCapacity(4);
		room.setNumOfResidents(0);
		room.setResidents(new ArrayList<>());
		Long roomId = RestAssured.given()
				.auth().oauth2(adminToken)
				.contentType("application/json")
				.body(room)
				.when().post("/api/rooms")
				.then().statusCode(201)
				.extract().jsonPath().getLong("id");

		Resident resident = new Resident();
		resident.setName("Alice Test");
		resident.setUsername("alice");
		resident.setEmail("alice@example.com");
		resident.setPhone("555-1111");
		resident.setRoom(null);
		Long residentId = RestAssured.given()
				.auth().oauth2(adminToken)
				.contentType("application/json")
				.body(resident)
				.when().post("/api/resident")
				.then().statusCode(201)
				.extract().jsonPath().getLong("id");

		RestAssured.given()
				.auth().oauth2(adminToken)
				.when().post("/api/resident/" + residentId + "/assign/" + roomId)
				.then().statusCode(204);

		RestAssured.given()
				.auth().oauth2(adminToken)
				.when().post("/api/resident/" + residentId + "/unassign")
				.then().statusCode(204);
	}

	@Test
	void onlyAdminCanAssignOrUnassign() {
		Room room = new Room();
		room.setNumber("B555");
		room.setCapacity(5);
		room.setNumOfResidents(0);
		room.setResidents(new ArrayList<>());
		Long roomId = RestAssured.given()
				.auth().oauth2(adminToken)
				.contentType("application/json")
				.body(room)
				.when().post("/api/rooms")
				.then().statusCode(201)
				.extract().jsonPath().getLong("id");

		Resident resident = new Resident();
		resident.setName("Bob OnlyUser");
		resident.setUsername("bob");
		resident.setEmail("bob@example.com");
		resident.setPhone("555-9999");
		resident.setRoom(null);
		Long residentId = RestAssured.given()
				.auth().oauth2(adminToken)
				.contentType("application/json")
				.body(resident)
				.when().post("/api/resident")
				.then().statusCode(201)
				.log().all()
				.extract().jsonPath().getLong("id");

		RestAssured.given()
				.auth().oauth2(userToken)
				.when().post("/api/resident/" + residentId + "/assign/" + roomId)
				.then().statusCode(403)
						.log().all();

		RestAssured.given()
				.auth().oauth2(userToken)
				.when().post("/api/resident/" + residentId + "/unassign")
				.then().statusCode(403)
				.log().all();
	}
}
