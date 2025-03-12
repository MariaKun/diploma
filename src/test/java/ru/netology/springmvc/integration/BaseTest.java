package ru.netology.springmvc.integration;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.springmvc.model.FileDTO;
import ru.netology.springmvc.service.FileService;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@Testcontainers
public class BaseTest {
    @LocalServerPort
    protected Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    protected MockMultipartFile file = new MockMultipartFile(
            "filename", "test.txt", "text/plain", "text".getBytes());

    protected long userId = 1L;
    protected final String invalidStr = "";

    @Autowired
    FileService fileService;

    @Container
    private static final GenericContainer<?> devApp = new GenericContainer<>("myapp:latest")
            .withExposedPorts(8080);

    @BeforeAll
    public static void setUp() {
        postgres.start();
        devApp.start();
    }

    @BeforeEach
    public void setUpEach() {
        List<FileDTO> allFiles = fileService.getAllFiles(userId, 100);
        for (FileDTO file : allFiles) {
            fileService.delete(userId, file.getFilename());
        }
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
}
