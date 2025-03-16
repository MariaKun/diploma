package ru.netology.springmvc.integration;

import com.google.gson.Gson;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.javatuples.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.model.FileDTO;
import ru.netology.springmvc.model.FileNameEditRequest;
import ru.netology.springmvc.service.FileService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.springmvc.TestData.*;

public class FileTests extends BaseTest {

    private final MockMultipartFile file = randomFile();

    @Autowired
    FileService fileService;

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

    private final MultiPartSpecification multiPartSpecification = new MultiPartSpecBuilder(file.getBytes())
            .fileName(randomFileName())
            .controlName(fileControlName)
            .mimeType(file.getContentType())
            .build();

    public FileTests() throws IOException {
    }

    @Test
    void uploadFile() {
        Pair<User, String> user = createUserAndGetToken();

        given()
                .multiPart(multiPartSpecification)
                .queryParam(filenameParam, multiPartSpecification.getFileName())
                .header(headerName, user.getValue1())
                .post(fileUri)
                .then().assertThat().statusCode(HttpStatus.OK.value());

        List<FileDTO> allFiles = fileService.getAllFiles(user.getValue0().getId(), 10);
        assertEquals(1, allFiles.size());
        assertEquals(multiPartSpecification.getFileName(), allFiles.get(0).getFilename());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void upload_emptyFilename(String filename) {
        Pair<User, String> user = createUserAndGetToken();

        String body = given()
                .multiPart(multiPartSpecification)
                .queryParam(filenameParam, filename)
                .header(headerName, user.getValue1())
                .post(fileUri)
                .then()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response().body().asString();
        assertThat(body, containsString("Invalid filename"));
    }

    @Test
    void upload_fileExist() {
        Pair<User, String> user = createUserAndGetToken();

        fileService.upload(user.getValue0().getId(), multiPartSpecification.getFileName(), file);

        String body = given()
                .multiPart(multiPartSpecification)
                .queryParam(filenameParam, multiPartSpecification.getFileName())
                .header(headerName, user.getValue1())
                .post(fileUri)
                .then()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response().getBody().asString();
        assertThat(body, containsString("File already exist"));
    }

    @Test
    void deleteFile() {
        Pair<User, String> user = createUserAndGetToken();

        String filename = randomFileName();
        fileService.upload(user.getValue0().getId(), filename, file);

        given()
                .queryParam(filenameParam, filename)
                .header(headerName, user.getValue1())
                .delete(fileUri)
                .then().assertThat().statusCode(HttpStatus.OK.value());
        List<FileDTO> allFiles = fileService.getAllFiles(user.getValue0().getId(), 10);
        assertEquals(0, allFiles.size());
    }

    @Test
    void deleteFile_fileNotFound() {
        Pair<User, String> user = createUserAndGetToken();

        given()
                .queryParam(filenameParam, randomFileName())
                .header(headerName, user.getValue1())
                .delete(fileUri)
                .then().assertThat().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void delete_emptyFilename(String filename) {
        Pair<User, String> user = createUserAndGetToken();
        String body = given()
                .queryParam(filenameParam, filename)
                .header(headerName, user.getValue1())
                .delete(fileUri)
                .then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response().getBody().asString();
        assertThat(body, containsString("Invalid filename"));
    }

    @Test
    void downloadFile() throws IOException {
        Pair<User, String> user = createUserAndGetToken();

        fileService.upload(user.getValue0().getId(), file.getName(), file);

        Response filecontent = given()
                .queryParam(filenameParam, file.getName())
                .header(headerName, user.getValue1())
                .get(fileUri)
                .then().assertThat().statusCode(HttpStatus.OK.value()).extract().response();

        assertEquals(file.getBytes().length, filecontent.asByteArray().length);
    }

    @Test
    void download_fileNotFound() {
        Pair<User, String> user = createUserAndGetToken();

        given()
                .queryParam(filenameParam, randomFileName())
                .header(headerName, user.getValue1())
                .get(fileUri)
                .then().assertThat().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void editFileName() {
        Pair<User, String> user = createUserAndGetToken();

        String newFilename = randomFileName();
        fileService.upload(user.getValue0().getId(), file.getName(), file);
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(newFilename);
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);

        given()
                .contentType(ContentType.JSON)
                .queryParam(filenameParam, file.getName())
                .header(headerName, user.getValue1())
                .body(json)
                .put(fileUri)
                .then().assertThat().statusCode(HttpStatus.OK.value());
        List<FileDTO> allFiles = fileService.getAllFiles(user.getValue0().getId(), 10);

        assertEquals(1, allFiles.size());
        assertEquals(newFilename, allFiles.get(0).getFilename());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void editFileName_emptyFileName(String filename) {
        Pair<User, String> user = createUserAndGetToken();

        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(randomFileName());
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);

        String body = given()
                .contentType(ContentType.JSON)
                .queryParam(filenameParam, filename)
                .header(headerName, user.getValue1())
                .body(json)
                .put(fileUri)
                .then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response().getBody().asString();
        assertThat(body, containsString("Invalid filename"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void editFileName_emptyNewFileName(String filename) {
        Pair<User, String> user = createUserAndGetToken();

        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(filename);
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        String body = given()
                .contentType(ContentType.JSON)
                .queryParam(filenameParam, file.getName())
                .header(headerName, user.getValue1())
                .body(json)
                .put(fileUri)
                .then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response().getBody().asString();
        assertThat(body, containsString("Invalid new filename"));
    }

    @Test
    void editFileName_fileNotFound() {
        Pair<User, String> user = createUserAndGetToken();

        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(randomFileName());
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        given()
                .contentType(ContentType.JSON)
                .queryParam(filenameParam, randomFileName())
                .header(headerName, user.getValue1())
                .body(json)
                .put(fileUri)
                .then().assertThat().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getAllFiles() {
        Pair<User, String> user = createUserAndGetToken();

        fileService.upload(user.getValue0().getId(), file.getName(), file);

        Response response = given()
                .contentType(ContentType.JSON)
                .queryParam(limitParam, 100)
                .header(headerName, user.getValue1())
                .get(listUri)
                .then().assertThat().statusCode(HttpStatus.OK.value()).extract().response();
        List<FileDTO> fileDTOList = Arrays.asList(response.getBody().as(FileDTO[].class));
        assertEquals(1, fileDTOList.size());
        assertEquals(file.getName(), fileDTOList.get(0).getFilename());
        assertEquals(file.getSize(), fileDTOList.get(0).getSize());
    }

    @Test
    void getAllFiles_severalUsers() {
        Pair<User, String> user1 = createUserAndGetToken();
        Pair<User, String> user2 = createUserAndGetToken();

        fileService.upload(user1.getValue0().getId(), file.getName(), file);
        fileService.upload(user2.getValue0().getId(), file.getName(), file);

        Response response = given()
                .contentType(ContentType.JSON)
                .queryParam(limitParam, 100)
                .header(headerName, user1.getValue1())
                .get(listUri)
                .then().assertThat().statusCode(HttpStatus.OK.value())
                .extract().response();
        List<FileDTO> fileDTOList = Arrays.asList(response.getBody().as(FileDTO[].class));
        assertEquals(1, fileDTOList.size());
        assertEquals(file.getName(), fileDTOList.get(0).getFilename());
        assertEquals(file.getSize(), fileDTOList.get(0).getSize());
    }

    @Test
    void getAllFiles_invalidLimit() {
        Pair<User, String> user = createUserAndGetToken();

        String body = given()
                .contentType(ContentType.JSON)
                .queryParam(limitParam, 0)
                .header(headerName, user.getValue1())
                .get(listUri)
                .then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response().getBody().asString();
        assertThat(body, containsString("Invalid limit"));
    }
}