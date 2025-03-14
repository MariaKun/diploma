package ru.netology.springmvc.integration;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.model.SignInRequest;
import ru.netology.springmvc.model.SignUpRequest;

import static io.restassured.RestAssured.given;

public class AuthTests extends BaseTest{

    @Test
    void signup()
    {
        SignUpRequest signUpRequest = randomSignUpRequest();
        String json = new Gson().toJson(signUpRequest, SignUpRequest.class);
        RestAssured.baseURI = "http://localhost:" + port;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .post("/signup")
                .then().assertThat().statusCode(200);
    }

    @Test
    void login()
    {
        SignUpRequest signUpRequest = randomSignUpRequest();
        authenticationService.signUp(signUpRequest);

        SignInRequest signInRequest = new SignInRequest(
                signUpRequest.getUsername(),
                signUpRequest.getPassword());
        String json = new Gson().toJson(signInRequest, SignInRequest.class);
        RestAssured.baseURI = "http://localhost:" + port;
        Response response = given()
                .contentType(ContentType.JSON)
                .body(json)
                .post("/login")
                .then().assertThat().statusCode(200).extract().response();
    }

    @Test
    void login_userNotExist()
    {
        SignInRequest signInRequest = new SignInRequest(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5));
        String json = new Gson().toJson(signInRequest, SignInRequest.class);
        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .contentType(ContentType.JSON)
                .body(json)
                .post("/login")
                .then().assertThat().statusCode(400).extract().response();
    }
}
