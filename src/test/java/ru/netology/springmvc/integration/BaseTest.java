package ru.netology.springmvc.integration;


import io.restassured.RestAssured;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.jwt.JwtService;
import ru.netology.springmvc.model.JwtAuthenticationResponse;
import ru.netology.springmvc.model.SignUpRequest;
import ru.netology.springmvc.service.AuthenticationService;
import ru.netology.springmvc.service.UserService;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:schema.sql"})
@Testcontainers
public class BaseTest {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @LocalServerPort
    protected Integer port;

    @BeforeEach
    void setupUri() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    protected Pair<User, String> createUserAndGetToken() {
        SignUpRequest signUpRequest = randomSignUpRequest();
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signUp(signUpRequest);
        User user = userService.getByUsername(signUpRequest.getUsername());
        return new Pair<>(user, "Bearer " + jwtAuthenticationResponse.getToken());
    }

    protected SignUpRequest randomSignUpRequest() {
        return new SignUpRequest(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5) + "test.com",
                RandomStringUtils.randomAlphanumeric(5));
    }
}
