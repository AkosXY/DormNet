package com.dormnet.resourceservice;

import com.dormnet.resourceservice.mock.TestJwtDecoderConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestJwtDecoderConfig.class)
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

	private String adminToken = "admin-token";

	@Test
	void testCreateResource() {
		String resourceJson = "{\"name\": \"Test Resource\", \"available\": true}";

		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson)
				.when()
				.post("/api/resource/add")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Test Resource"))
				.body("available", equalTo(true));
	}


	@Test
	void testCreateAndGetAvailableResource() {
		String resourceJson = "{\"name\": \"Test Resource\", \"available\": true}";

		String resourceId = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson)
				.when()
				.post("/api/resource/add")
				.then()
				.log().all()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Test Resource"))
				.body("available", equalTo(true))
				.extract()
				.path("id").toString();


		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.when()
				.get("/api/resource/available?id=" + resourceId)
				.then()
				.log().all()
				.statusCode(200)
				.body(equalTo("true"));
	}

	@Test
	void testCreateAndGetUnavailableResource() {
		String resourceJson = "{\"name\": \"Test Resource\", \"available\": false}";

		String resourceId = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson)
				.when()
				.post("/api/resource/add")
				.then()
				.log().all()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Test Resource"))
				.body("available", equalTo(false))
				.extract()
				.path("id").toString();


		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.when()
				.get("/api/resource/available?id=" + resourceId)
				.then()
				.log().all()
				.statusCode(200)
				.body(equalTo("false"));
	}

	@Test
	void testMakeAvailableResource() {
		String resourceJson = "{\"name\": \"Test Resource\", \"available\": false}";

		String resourceId = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson)
				.when()
				.post("/api/resource/add")
				.then()
				.log().all()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Test Resource"))
				.body("available", equalTo(false))
				.extract()
				.path("id").toString();

		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.when()
				.post("/api/resource/makeAvailable?id=" + resourceId)
				.then()
				.log().all()
				.statusCode(200);

		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.when()
				.get("/api/resource/available?id=" + resourceId)
				.then()
				.log().all()
				.statusCode(200)
				.body(equalTo("true"));
	}

}
