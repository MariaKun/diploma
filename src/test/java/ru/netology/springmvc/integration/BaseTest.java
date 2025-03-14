package ru.netology.springmvc.integration;


import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.javatuples.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.jwt.JwtService;
import ru.netology.springmvc.model.FileDTO;
import ru.netology.springmvc.model.JwtAuthenticationResponse;
import ru.netology.springmvc.model.SignInRequest;
import ru.netology.springmvc.model.SignUpRequest;
import ru.netology.springmvc.service.AuthenticationService;
import ru.netology.springmvc.service.FileService;
import ru.netology.springmvc.service.UserService;

import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@Testcontainers
public class BaseTest {
    @LocalServerPort
    protected Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    protected MockMultipartFile file = new MockMultipartFile(
            "filename", "test.txt", "text/plain", "text".getBytes());

    protected final String invalidStr = "";

    protected final String headerName = "auth-token";

    @Autowired
    FileService fileService;

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

    @Container
    private static final GenericContainer<?> devApp = new GenericContainer<>("myapp:latest")
            .withExposedPorts(8080);

    protected static RequestSpecification spec ;

    @BeforeAll
    public static void setUp() {
       // RestAssured.baseURI = "http://localhost:" + devApp.getMappedPort(8080);
        postgres.start();
        devApp.start();

    }


    @AfterAll
    static void afterAll() {
        postgres.stop();
        devApp.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    protected String randomFileName()
    {
        return RandomStringUtils.randomAlphabetic(5) + "." + RandomStringUtils.randomAlphabetic(3);
    }

    protected Pair<User, String> createUserAndGetToken()
    {
        SignUpRequest signUpRequest = randomSignUpRequest();
        authenticationService.signUp(signUpRequest);

        SignInRequest signInRequest = new SignInRequest(
                signUpRequest.getUsername(), 
                signUpRequest.getPassword());
        JwtAuthenticationResponse login = authenticationService.login(signInRequest);
        User user = userService.getByUsername(signUpRequest.getUsername());
        return new Pair<>(user, "Bearer " + login.getToken());
    }

    protected SignUpRequest randomSignUpRequest()
    {
       return new SignUpRequest(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5) + "@test.com",
                RandomStringUtils.randomAlphanumeric(5));
    }
}
