package com.dormnet.sportservice;

import com.dormnet.sportservice.mock.TestJwtDecoderConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestJwtDecoderConfig.class)
class SportServiceApplicationTests {

	@LocalServerPort
	private Integer port;

	@BeforeEach
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	private String adminToken = "admin-token";

	@Test
	void testCreateEvent() {
		String sportEventJson = "{\"name\": \"Test Name\", \"date\": \"2024-05-15\"}";

		given()
				.auth().oauth2(adminToken)
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
	@Test
	void testGetAllSportEvents() {
		testCreateEvent();
		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.when()
				.get("/api/sport")
				.then()
				.log().all()
				.statusCode(200)
				.body("size()", Matchers.greaterThan(0));
	}

	@Test
	void testAddEntryToSportEvent() {
		String sportEventJson = "{\"name\": \"Test Name\", \"date\": \"2024-05-15\"}";

		String eventId = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(sportEventJson)
				.when()
				.post("/api/sport")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Test Name"))
				.body("date", equalTo("2024-05-15"))
				.extract()
				.path("id");


		String entryJson = "{\"participantName\": \"Test User\", \"score\": 55}";

		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(entryJson)
				.when()
				.post("/api/sport/" + eventId + "/add_entries")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("entries[0].participantName", Matchers.equalTo("Test User"))
				.body("entries[0].score", equalTo(55));
	}
	@Test
	public void testCreateAndDeleteSportEvent() {
		String sportEventJson = "{\"name\": \"Delete Test Event\", \"date\": \"2024-05-15\"}";

		String eventId = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(sportEventJson)
				.when()
				.post("/api/sport")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.extract()
				.path("id");

		given()
				.auth().oauth2(adminToken)
				.when()
				.delete("/api/sport/" + eventId)
				.then()
				.statusCode(204);

    given()
			.auth().oauth2(adminToken)
			.when()
            .get("/api/sport/")
            .then()
            .statusCode(404);
	}


}
