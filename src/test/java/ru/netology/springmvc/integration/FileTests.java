package ru.netology.springmvc.integration;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
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
        String filename = RandomStringUtils.random(5, true, false);
        MultiPartSpecification multiPartSpecification = new MultiPartSpecBuilder("File content".getBytes())
                .fileName(filename)
                .controlName("file")
                .mimeType(file.getContentType())
                .build();

        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .multiPart(multiPartSpecification)
                .queryParam("filename", filename)
                .post("/file").then().assertThat().statusCode(200);
        List<FileDTO> allFiles = fileService.getAllFiles(userId, 10);
        assertEquals(1, allFiles.size());
        assertEquals(filename, allFiles.get(0).getFilename());
    }

    @Test
    void upload_invalidFileName() {
        RestAssured.baseURI = "http://localhost:" + port;
        MultiPartSpecification multiPartSpecification = new MultiPartSpecBuilder("File content".getBytes())
                .fileName(file.getName())
                .controlName("file")
                .mimeType(file.getContentType())
                .build();

        given()
                .multiPart(multiPartSpecification)
                .queryParam("filename", invalidStr)
                .post("/file").then().assertThat().statusCode(400);
    }

    @Test
    void upload_fileExist() {
        String filename = RandomStringUtils.random(5, true, false);
        fileService.upload(userId, filename, file);
        RestAssured.baseURI = "http://localhost:" + port;
        MultiPartSpecification multiPartSpecification = new MultiPartSpecBuilder("File content".getBytes())
                .fileName(filename)
                .controlName("file")
                .mimeType(file.getContentType())
                .build();

        given()
                .multiPart(multiPartSpecification)
                .queryParam("filename", filename)
                .post("/file").then().assertThat().statusCode(400);
    }

    @Test
    void deleteFile() {
        String filename = RandomStringUtils.random(5, true, false);
        fileService.upload(userId, filename, file);
        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .queryParam("filename", filename)
                .delete("/file").then().assertThat().statusCode(200);
        List<FileDTO> allFiles = fileService.getAllFiles(userId, 10);
        assertEquals(0, allFiles.size());
    }

    @Test
    void deleteFile_fileNotFound() {
        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .queryParam("filename", "notExist")
                .delete("/file").then().assertThat().statusCode(404);
    }

    @Test
    void delete_invalidFileName() {
        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .queryParam("filename", invalidStr)
                .delete("/file").then().assertThat().statusCode(400);
    }

    @Test
    void downloadFile() throws IOException {
        String filename = RandomStringUtils.random(5, true, false);
        fileService.upload(userId, filename, file);
        RestAssured.baseURI = "http://localhost:" + port;
        Response filecontent = given()
                .queryParam("filename", filename)
                .get("/file")
                .then().assertThat().statusCode(200).extract().response();
        assertEquals(file.getBytes().length, filecontent.asByteArray().length);
    }

    @Test
    void download_fileNotFound() {
        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .queryParam("filename", "notExist")
                .contentType("text/plain")
                .get("/file")
                .then().assertThat().statusCode(404);
    }

    @Test
    void editFileName() {
        String filename = RandomStringUtils.random(5, true, false);
        fileService.upload(userId, filename, file);
        RestAssured.baseURI = "http://localhost:" + port;
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest("newName");
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        given()
                .contentType(ContentType.JSON)
                .queryParam("filename", filename)
                .body(json)
                .put("/file").then().assertThat().statusCode(200);
        List<FileDTO> allFiles = fileService.getAllFiles(userId, 10);
        assertEquals(1, allFiles.size());
        assertEquals("newName", allFiles.get(0).getFilename());
    }

    @Test
    void editFileName_invalidFileName() {
        RestAssured.baseURI = "http://localhost:" + port;
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest("newName");
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        given()
                .contentType(ContentType.JSON)
                .queryParam("filename", invalidStr)
                .body(json)
                .put("/file").then().assertThat().statusCode(400);
    }

    @Test
    void editFileName_invalidNewFileName() {
        RestAssured.baseURI = "http://localhost:" + port;
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(invalidStr);
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        given()
                .contentType(ContentType.JSON)
                .queryParam("filename", file.getName())
                .body(json)
                .put("/file").then().assertThat().statusCode(400);
    }

    @Test
    void editFileName_fileNotFound() {
        RestAssured.baseURI = "http://localhost:" + port;
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest("newName");
        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        given()
                .contentType(ContentType.JSON)
                .queryParam("filename", "notExist")
                .body(json)
                .put("/file").then().assertThat().statusCode(404);
    }

    @Test
    void getAllFiles() {
        String filename = RandomStringUtils.random(5, true, false);
        fileService.upload(userId, filename, file);
        RestAssured.baseURI = "http://localhost:" + port;
        Response response = given()
                .contentType(ContentType.JSON)
                .queryParam("limit", 100)
                .get("/list")
                .then().assertThat().statusCode(200).extract().response();
        List<FileDTO> fileDTOList = Arrays.asList(response.getBody().as(FileDTO[].class));
        assertEquals(filename, fileDTOList.get(0).getFilename());
        assertEquals(file.getSize(), fileDTOList.get(0).getSize());
    }

    @Test
    void getAllFiles_invalidLimit() {
        RestAssured.baseURI = "http://localhost:" + port;
        given()
                .contentType(ContentType.JSON)
                .queryParam("limit", 0)
                .get("/list")
                .then().assertThat().statusCode(400);
    }
}