package ru.netology.springmvc.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.netology.springmvc.exception.ErrorInputData;
import ru.netology.springmvc.exception.FileNotFound;
import ru.netology.springmvc.model.FileNameEditRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FileControllerTest extends BaseControllerTest {

    FileControllerTest() throws IOException {
    }

    @Test
    void uploadFile() throws Exception {
        RequestBuilder request =  multipart("/file")
                .file("file", file.getBytes())
                .param("filename", file.getName())
                .header(headerName, token)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void uploadFile_errorInputData() throws Exception {
        when(service.upload(testUser.getId(), validFilename, null)).thenThrow(ErrorInputData.class);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/file")
                .param("filename", validFilename)
                .header(headerName, token);
        mockMvc.perform(request).andExpect(status().isBadRequest());
        verify(service, times(1)).upload(testUser.getId(), validFilename, null);
    }

    @Test
    void deleteFile() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/file")
                .header(headerName, token)
                .param("filename", fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isOk());
        verify(service, times(1)).delete(testUser.getId(), fileEntity.getFilename());
    }

    @Test
    void deleteFile_fileNotFound() throws Exception {
        doThrow(FileNotFound.class).when(service).delete(testUser.getId(), fileEntity.getFilename());
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/file")
                .header(headerName, token)
                .param("filename", fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isNotFound());
        verify(service, times(1)).delete(testUser.getId(), file.getName());
    }

    @Test
    void delete_invalidFileName() throws Exception {
        doThrow(ErrorInputData.class).when(service).delete(testUser.getId(), invalidStr);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/file")
                .header(headerName, token)
                .param("filename", invalidStr);
        this.mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void downloadFile() throws Exception {
        when(service.download(testUser.getId(), fileEntity.getFilename())).thenReturn(fileEntity);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/file")
                .header(headerName, token)
                .param("filename", fileEntity.getFilename());
        MvcResult mvcResult = this.mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        byte[] contentAsByteArray = mvcResult.getResponse().getContentAsByteArray();
        assertEquals(fileEntity.getFilecontent().length, contentAsByteArray.length);
    }

    @Test
    void editFileName() throws Exception {
        FileNameEditRequest fileNameEditRequest = new FileNameEditRequest(validFilename);
        doNothing().when(service).editFileName(testUser.getId(), validFilename, fileNameEditRequest);

        String json = new Gson().toJson(fileNameEditRequest, FileNameEditRequest.class);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/file")
                .header(headerName, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .param("filename", fileEntity.getFilename());
        this.mockMvc.perform(request).andExpect(status().isOk());
        verify(service, times(1)).editFileName(testUser.getId(), validFilename, fileNameEditRequest);
    }
}