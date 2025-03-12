package ru.netology.springmvc.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.netology.springmvc.exception.ErrorInputData;
import ru.netology.springmvc.exception.FileNotFound;
import ru.netology.springmvc.model.FileNameEditRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
class FileControllerTest extends BaseControllerTest {

    FileControllerTest() throws IOException {
    }

    @Test
    void uploadFile() throws Exception {
        when(service.upload(userId1, validFilename, file)).thenReturn(fileEntity);
        RequestBuilder request = MockMvcRequestBuilders
                .multipart("/file")
                .file(file)
                .param("filename", validFilename);
        mockMvc.perform(request);
        verify(service, times(1)).upload(userId1, validFilename, file);
    }

    @Test
    void deleteFile() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/file")
                .param("filename", fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isOk());
        verify(service, times(1)).delete(userId1, fileEntity.getFilename());
    }

    @Test
    void deleteFile_fileNotFound() throws Exception {
        doThrow(FileNotFound.class).when(service).delete(userId1, fileEntity.getFilename());
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/file")
                .param("filename", fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    void delete_invalidFileName() throws Exception {
        doThrow(ErrorInputData.class).when(service).delete(userId1, invalidStr);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/file")
                .param("filename", invalidStr);
        this.mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void downloadFile() throws Exception {
        when(service.download(userId1, fileEntity.getFilename())).thenReturn(fileEntity);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/file")
                .param("filename", fileEntity.getFilename());
        MvcResult mvcResult = this.mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        byte[] contentAsByteArray = mvcResult.getResponse().getContentAsByteArray();
        assertEquals(fileEntity.getFilecontent().length, contentAsByteArray.length);
    }

    @Test
    void editFileName() throws Exception {
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(validFilename);
        doNothing().when(service).editFileName(userId1, validFilename, fileNameEditRequest);

        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .param("filename", fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isOk());
        verify(service, times(1)).editFileName(userId1, validFilename, fileNameEditRequest);
    }
}