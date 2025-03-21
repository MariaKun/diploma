package ru.netology.springmvc.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.springmvc.TestData.*;

@SpringBootTest
@AutoConfigureMockMvc
class FileControllerTest extends BaseControllerTest {

    FileControllerTest() throws IOException {
    }

    @Test
    void uploadFile() throws Exception {
        RequestBuilder request = multipart(fileUri)
                .file(fileControlName, testFile.getBytes())
                .param(filenameParam, testFile.getName())
                .header(headerName, getToken(testUser))
                .contentType(MediaType.MULTIPART_FORM_DATA);
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void uploadFile_errorInputData() throws Exception {
        when(service.upload(testUser.getId(), testFile.getName(), null)).thenThrow(ErrorInputData.class);

        RequestBuilder request = MockMvcRequestBuilders
                .post(fileUri)
                .param(filenameParam, testFile.getName())
                .header(headerName, getToken(testUser));
        mockMvc.perform(request).andExpect(status().isBadRequest());
        verify(service, times(1)).upload(testUser.getId(), testFile.getName(), null);
    }

    @Test
    void deleteFile() throws Exception {
        doNothing().when(service).delete(testUser.getId(), fileEntity.getFilename());

        RequestBuilder request = MockMvcRequestBuilders
                .delete(fileUri)
                .header(headerName, getToken(testUser))
                .param(filenameParam, fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isOk());
        verify(service, times(1)).delete(testUser.getId(), fileEntity.getFilename());
    }

    @Test
    void deleteFile_fileNotFound() throws Exception {
        doThrow(FileNotFound.class).when(service).delete(testUser.getId(), fileEntity.getFilename());

        RequestBuilder request = MockMvcRequestBuilders
                .delete(fileUri)
                .header(headerName, getToken(testUser))
                .param(filenameParam, fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isNotFound());
        verify(service, times(1)).delete(testUser.getId(), fileEntity.getFilename());
    }

    @Test
    void delete_errorInputData() throws Exception {
        doThrow(ErrorInputData.class).when(service).delete(testUser.getId(), fileEntity.getFilename());

        RequestBuilder request = MockMvcRequestBuilders
                .delete(fileUri)
                .header(headerName, getToken(testUser))
                .param(filenameParam, fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isBadRequest());
        verify(service, times(1)).delete(testUser.getId(), fileEntity.getFilename());
    }

    @Test
    void downloadFile() throws Exception {
        when(service.download(testUser.getId(), fileEntity.getFilename())).thenReturn(fileEntity);

        RequestBuilder request = MockMvcRequestBuilders
                .get(fileUri)
                .header(headerName, getToken(testUser))
                .param(filenameParam, fileEntity.getFilename());
        MvcResult mvcResult = this.mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        byte[] contentAsByteArray = mvcResult.getResponse().getContentAsByteArray();
        assertEquals(fileEntity.getFilecontent().length, contentAsByteArray.length);
        verify(service, times(1)).download(testUser.getId(), fileEntity.getFilename());
    }

    @Test
    void downloadFile_errorInputData() throws Exception {
        when(service.download(testUser.getId(), fileEntity.getFilename())).thenThrow(ErrorInputData.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get(fileUri)
                .header(headerName, getToken(testUser))
                .param(filenameParam, fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isBadRequest());
        verify(service, times(1)).download(testUser.getId(), fileEntity.getFilename());
    }

    @Test
    void downloadFile_fileNotFound() throws Exception {
        when(service.download(testUser.getId(), fileEntity.getFilename())).thenThrow(FileNotFound.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get(fileUri)
                .header(headerName, getToken(testUser))
                .param(filenameParam, fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isNotFound());
        verify(service, times(1)).download(testUser.getId(), fileEntity.getFilename());
    }

    @Test
    void editFileName() throws Exception {
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(randomFileName());
        doNothing().when(service).editFileName(testUser.getId(), testFile.getName(), fileNameEditRequest);

        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        RequestBuilder request = MockMvcRequestBuilders
                .put(fileUri)
                .header(headerName, getToken(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .param(filenameParam, fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isOk());
        verify(service, times(1)).editFileName(testUser.getId(), testFile.getName(), fileNameEditRequest);
    }

    @Test
    void editFileName_errorInputData() throws Exception {
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest("");
        doThrow(ErrorInputData.class).when(service).editFileName(testUser.getId(), testFile.getName(), fileNameEditRequest);

        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        RequestBuilder request = MockMvcRequestBuilders
                .put(fileUri)
                .header(headerName, getToken(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .param(filenameParam, fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isBadRequest());
        verify(service, times(1)).editFileName(testUser.getId(), testFile.getName(), fileNameEditRequest);
    }

    @Test
    void editFileName_fileNotFound() throws Exception {
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(randomFileName());
        doThrow(FileNotFound.class).when(service).editFileName(testUser.getId(), testFile.getName(), fileNameEditRequest);

        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        RequestBuilder request = MockMvcRequestBuilders
                .put(fileUri)
                .header(headerName, getToken(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .param(filenameParam, fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isNotFound());
        verify(service, times(1)).editFileName(testUser.getId(), testFile.getName(), fileNameEditRequest);
    }
}