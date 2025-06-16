package com.dormnet.resourceservice;

import com.dormnet.resourceservice.kafka.config.ResourceEventProducer;
import com.dormnet.resourceservice.kafka.dto.ResourceUnavailableEvent;
import com.dormnet.resourceservice.mock.TestJwtDecoderConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestJwtDecoderConfig.class)
class ResourceServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.35");

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

	@MockBean
	private ResourceEventProducer eventProducer;

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

	@Test
	void testMakeAvailableResource_nonExistent() {
		Long nonExistentId = 999998L;

		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.when()
				.post("/api/resource/makeAvailable?id=" + nonExistentId)
				.then()
				.log().all()
				.body(equalTo("false"));
	}


	@Test
	void testGetResources() {
		String resourceJson1 = "{\"name\": \"Resource1\", \"available\": true}";
		String resourceJson2 = "{\"name\": \"Resource2\", \"available\": false}";

		String id1 = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson1)
				.when()
				.post("/api/resource/add")
				.then()
				.statusCode(201)
				.extract().path("id").toString();

		String id2 = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson2)
				.when()
				.post("/api/resource/add")
				.then()
				.statusCode(201)
				.extract().path("id").toString();

		given()
				.auth().oauth2(adminToken)
				.when()
				.get("/api/resource/getResources")
				.then()
				.statusCode(200)
				.body("id", Matchers.hasItems(Integer.parseInt(id1), Integer.parseInt(id2)));
	}

	@Test
	void testGetAvailableResources() {
		String resourceJson1 = "{\"name\": \"ResA\", \"available\": true}";
		String resourceJson2 = "{\"name\": \"ResB\", \"available\": false}";

		String idAv = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson1)
				.when()
				.post("/api/resource/add")
				.then()
				.statusCode(201)
				.extract().path("id").toString();

		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson2)
				.when()
				.post("/api/resource/add")
				.then()
				.statusCode(201);

		given()
				.auth().oauth2(adminToken)
				.when()
				.get("/api/resource/availableResources")
				.then()
				.statusCode(200)
				.body("id", Matchers.hasItem(Integer.parseInt(idAv)))
				.body("available", Matchers.everyItem(Matchers.is(true)));
	}

	@Test
	void testMakeUnavailable() {
		String resourceJson = "{\"name\": \"TempRes\", \"available\": true}";

		String id = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson)
				.when()
				.post("/api/resource/add")
				.then()
				.statusCode(201)
				.extract().path("id").toString();

		given()
				.auth().oauth2(adminToken)
				.when()
				.post("/api/resource/makeUnavailable?id=" + id)
				.then()
				.statusCode(200)
				.body(equalTo("true"));

		given()
				.auth().oauth2(adminToken)
				.when()
				.get("/api/resource/available?id=" + id)
				.then()
				.statusCode(200)
				.body(equalTo("false"));
	}

	@Test
	void testMakeUnavailableResource_nonExistent() {
		Long nonExistentId = 999998L;

		given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.when()
				.post("/api/resource/makeUnavailable?id=" + nonExistentId)
				.then()
				.log().all()
				.body(equalTo("false"));
	}


	@Test
	void testDeleteResource() {
		String resourceJson = "{\"name\": \"DeleteMe\", \"available\": false}";
		String resourceId = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson)
				.when()
				.post("/api/resource/add")
				.then()
				.statusCode(201)
				.extract().path("id").toString();

		given()
				.auth().oauth2(adminToken)
				.when()
				.get("/api/resource/getResources")
				.then()
				.body("id", Matchers.hasItem(Integer.parseInt(resourceId)));

		given()
				.auth().oauth2(adminToken)
				.when()
				.delete("/api/resource/delete?id=" + resourceId)
				.then()
				.statusCode(200)
				.body(equalTo("true"));

		given()
				.auth().oauth2(adminToken)
				.when()
				.get("/api/resource/getResources")
				.then()
				.body("id", Matchers.not(Matchers.hasItem(Integer.parseInt(resourceId))));
	}

	@Test
	void testDeleteResource_failsForAvailableResource() {
		String resourceJson = "{\"name\": \"ShouldNotDelete\", \"available\": true}";
		String resourceId = given()
				.auth().oauth2(adminToken)
				.contentType(ContentType.JSON)
				.body(resourceJson)
				.when()
				.post("/api/resource/add")
				.then()
				.statusCode(201)
				.extract().path("id").toString();

		given()
				.auth().oauth2(adminToken)
				.when()
				.delete("/api/resource/delete?id=" + resourceId)
				.then()
				.statusCode(200)
				.body(equalTo("false"));

		given()
				.auth().oauth2(adminToken)
				.when()
				.get("/api/resource/getResources")
				.then()
				.body("id", Matchers.hasItem(Integer.parseInt(resourceId)));
	}


	@Test
	void testSendResourceUnavailableEvent_sendsCorrectMessage() throws Exception {
		KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
		ObjectMapper objectMapper = new ObjectMapper();

		ResourceEventProducer producer = new ResourceEventProducer(kafkaTemplate, objectMapper);

		Long resourceId = 777L;
		producer.sendResourceUnavailableEvent(resourceId);

		ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

		verify(kafkaTemplate).send(topicCaptor.capture(), messageCaptor.capture());

		assertThat(topicCaptor.getValue()).isEqualTo("resource-unavailable");

		String expectedJson = objectMapper.writeValueAsString(new ResourceUnavailableEvent(resourceId));
		assertThat(messageCaptor.getValue()).isEqualTo(expectedJson);
	}

}
