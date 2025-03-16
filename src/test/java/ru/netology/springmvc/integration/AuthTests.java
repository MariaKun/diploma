package ru.netology.springmvc.integration;

import com.google.gson.Gson;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.model.SignInRequest;
import ru.netology.springmvc.model.SignUpRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static ru.netology.springmvc.TestData.loginUri;
import static ru.netology.springmvc.TestData.signupUri;

public class AuthTests extends BaseTest {

    @Container
    private static final GenericContainer<?> devApp = new GenericContainer<>("myapp:latest")
            .withExposedPorts(8080);

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    public static void setUp() {
        postgres.start();
        devApp.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        devApp.stop();
    }

    @Test
    void signup() {
        SignUpRequest signUpRequest = randomSignUpRequest();
        String json = new Gson().toJson(signUpRequest, SignUpRequest.class);

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .post(signupUri)
                .then().assertThat().statusCode(HttpStatus.OK.value());
    }

    @Test
    void signup_userAlreadyExists() {
        SignUpRequest signUpRequest = randomSignUpRequest();
        authenticationService.signUp(signUpRequest);
        String json = new Gson().toJson(signUpRequest, SignUpRequest.class);

        String body = given()
                .contentType(ContentType.JSON)
                .body(json)
                .post(signupUri)
                .then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value()).extract().response().getBody().asString();
        assertThat(body, containsString("User already exists"));
    }

    @Test
    void login() {
        SignUpRequest signUpRequest = randomSignUpRequest();
        authenticationService.signUp(signUpRequest);

        SignInRequest signInRequest = new SignInRequest(
                signUpRequest.getUsername(),
                signUpRequest.getPassword());
        String json = new Gson().toJson(signInRequest, SignInRequest.class);

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .post(loginUri)
                .then().assertThat().statusCode(HttpStatus.OK.value());
    }

    @Test
    void login_userNotExist() {
        SignInRequest signInRequest = new SignInRequest(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5));
        String json = new Gson().toJson(signInRequest, SignInRequest.class);

        String body = given()
                .contentType(ContentType.JSON)
                .body(json)
                .post(loginUri)
                .then().assertThat().statusCode(HttpStatus.NOT_FOUND.value())
                .extract().response().getBody().asString();
        assertThat(body, containsString("User not found"));
    }
}
