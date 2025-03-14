package ru.netology.springmvc.integration;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.javatuples.Pair;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.model.FileDTO;
import ru.netology.springmvc.model.FileNameEditRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileTests extends BaseTest {

    @Test
    void uploadFile() {
        Pair<User, String> user = createUserAndGetToken();

        String filename = randomFileName();
        MultiPartSpecification multiPartSpecification = new MultiPartSpecBuilder("File content".getBytes())
                .fileName(filename)
                .controlName("file")
                .mimeType(file.getContentType())
                .build();

        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .multiPart(multiPartSpecification)
                .queryParam("filename", filename)
                .header(headerName, user.getValue1())
                .post("/file").then().assertThat().statusCode(200);
        List<FileDTO> allFiles = fileService.getAllFiles(user.getValue0().getId(), 10);
        assertEquals(1, allFiles.size());
        assertEquals(filename, allFiles.get(0).getFilename());
    }

    @Test
    void upload_invalidFileName() {
        Pair<User, String> user = createUserAndGetToken();

        RestAssured.baseURI = "http://localhost:" + port;
        MultiPartSpecification multiPartSpecification = new MultiPartSpecBuilder("File content".getBytes())
                .fileName(file.getName())
                .controlName("file")
                .mimeType(file.getContentType())
                .build();

        given()
                .multiPart(multiPartSpecification)
                .queryParam("filename", "")
                .header(headerName, user.getValue1())
                .post("/file").then().assertThat().statusCode(400);
    }

    @Test
    void upload_fileExist() {
        Pair<User, String> user = createUserAndGetToken();

        String filename = randomFileName();
        fileService.upload(user.getValue0().getId(), filename, file);
        RestAssured.baseURI = "http://localhost:" + port;
        MultiPartSpecification multiPartSpecification = new MultiPartSpecBuilder("File content".getBytes())
                .fileName(filename)
                .controlName("file")
                .mimeType(file.getContentType())
                .build();

        given()
                .multiPart(multiPartSpecification)
                .queryParam("filename", filename)
                .header(headerName, user.getValue1())
                .post("/file").then().assertThat().statusCode(400);
    }

    @Test
    void deleteFile() {
        Pair<User, String> user = createUserAndGetToken();

        String filename = randomFileName();
        fileService.upload(user.getValue0().getId(), filename, file);
        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .queryParam("filename", filename)
                .header(headerName, user.getValue1())
                .delete("/file").then().assertThat().statusCode(200);
        List<FileDTO> allFiles = fileService.getAllFiles(user.getValue0().getId(), 10);
        assertEquals(0, allFiles.size());
    }

    @Test
    void deleteFile_fileNotFound() {
        Pair<User, String> user = createUserAndGetToken();

        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .queryParam("filename", "notExist")
                .header(headerName, user.getValue1())
                .delete("/file").then().assertThat().statusCode(404);
    }

    @Test
    void delete_invalidFileName() {
        Pair<User, String> user = createUserAndGetToken();
        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .queryParam("filename", invalidStr)
                .header(headerName, user.getValue1())
                .delete("/file").then().assertThat().statusCode(400);
    }

    @Test
    void downloadFile() throws IOException {
        Pair<User, String> user = createUserAndGetToken();

        String filename = randomFileName();
        fileService.upload(user.getValue0().getId(), filename, file);
        RestAssured.baseURI = "http://localhost:" + port;

        Response filecontent = given()
                .queryParam("filename", filename)
                .header(headerName, user.getValue1())
                .get("/file")
                .then().assertThat().statusCode(200).extract().response();

        assertEquals(file.getBytes().length, filecontent.asByteArray().length);
    }

    @Test
    void download_fileNotFound() {
        Pair<User, String> user = createUserAndGetToken();

        RestAssured.baseURI = "http://localhost:" + port;

        given()
                .queryParam("filename", "notExist")
                .header(headerName, user.getValue1())
                .contentType("text/plain")
                .get("/file")
                .then().assertThat().statusCode(404);
    }

    @Test
    void editFileName() {
        Pair<User, String> user = createUserAndGetToken();

        String filename = randomFileName();
        fileService.upload(user.getValue0().getId(), filename, file);
        RestAssured.baseURI = "http://localhost:" + port;
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest("newName");
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);

        given()
                .contentType(ContentType.JSON)
                .queryParam("filename", filename)
                .header(headerName, user.getValue1())
                .body(json)
                .put("/file").then().assertThat().statusCode(200);
        List<FileDTO> allFiles = fileService.getAllFiles(user.getValue0().getId(), 10);

        assertEquals(1, allFiles.size());
        assertEquals("newName", allFiles.get(0).getFilename());
    }

    @Test
    void editFileName_invalidFileName() {
        Pair<User, String> user = createUserAndGetToken();

        RestAssured.baseURI = "http://localhost:" + port;
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest("newName");
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);

        given()
                .contentType(ContentType.JSON)
                .queryParam("filename", invalidStr)
                .header(headerName, user.getValue1())
                .body(json)
                .put("/file").then().assertThat().statusCode(400);
    }

    @Test
    void editFileName_invalidNewFileName() {
        Pair<User, String> user = createUserAndGetToken();

        RestAssured.baseURI = "http://localhost:" + port;
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(invalidStr);
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        given()
                .contentType(ContentType.JSON)
                .queryParam("filename", file.getName())
                .header(headerName, user.getValue1())
                .body(json)
                .put("/file").then().assertThat().statusCode(400);
    }

    @Test
    void editFileName_fileNotFound() {
        Pair<User, String> user = createUserAndGetToken();

        RestAssured.baseURI = "http://localhost:" + port;
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest("newName");
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        given()
                .contentType(ContentType.JSON)
                .queryParam("filename", "notExist")
                .header(headerName, user.getValue1())
                .body(json)
                .put("/file").then().assertThat().statusCode(404);
    }

    @Test
    void getAllFiles() {
        Pair<User, String> user = createUserAndGetToken();

        String filename = RandomStringUtils.random(5, true, false);
        fileService.upload(user.getValue0().getId(), filename, file);
        RestAssured.baseURI = "http://localhost:" + port;
        Response response = given()
                .contentType(ContentType.JSON)
                .queryParam("limit", 100)
                .header(headerName, user.getValue1())
                .get("/list")
                .then().assertThat().statusCode(200).extract().response();
        List<FileDTO> fileDTOList = Arrays.asList(response.getBody().as(FileDTO[].class));
        assertEquals(filename, fileDTOList.get(0).getFilename());
        assertEquals(file.getSize(), fileDTOList.get(0).getSize());
    }

    @Test
    void getAllFiles_invalidLimit() {
        Pair<User, String> user = createUserAndGetToken();

        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .contentType(ContentType.JSON)
                .queryParam("limit", 0)
                .header(headerName, user.getValue1())
                .get("/list")
                .then().assertThat().statusCode(400);
    }
}