package ru.netology.springmvc.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.model.FileDTO;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListsTest extends BaseTest {

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
