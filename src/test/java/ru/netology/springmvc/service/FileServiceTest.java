package ru.netology.springmvc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.netology.springmvc.entity.Files;
import ru.netology.springmvc.exception.ErrorInputData;
import ru.netology.springmvc.exception.FileNotFound;
import ru.netology.springmvc.model.FileDTO;
import ru.netology.springmvc.model.FileNameEditRequest;
import ru.netology.springmvc.repository.FileRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    FileService fileService;
    @Mock
    FileRepository fileRepository;

    private final String validFilename = "file";

    private final MockMultipartFile file = new MockMultipartFile(
            validFilename, "test.txt", "text/plain", "text".getBytes());

    private final long userId1 = 1L;

    private final Files fileEntity = Files.builder()
            .filecontent(file.getBytes())
            .size((int)file.getSize())
            .filename(file.getName())
            .type(file.getContentType())
            .userid(userId1).build();

    private final String invalidStr = "";

    FileServiceTest() throws IOException {
    }

    @Test
    void upload() {
        when(fileRepository.save(fileEntity)).thenReturn(fileEntity);

        Files upload = fileService.upload(userId1, file.getName(), file);
        assertEquals(fileEntity, upload);
    }

    @Test
    void upload_invalidFileName() {
        assertThrows(ErrorInputData.class, () -> fileService.upload(userId1, invalidStr, file));
    }

    @Test
    void upload_fileExist() {
        when(fileRepository.findByUseridAndFilename(userId1, validFilename)).thenReturn(fileEntity);
        assertThrows(ErrorInputData.class, () -> fileService.upload(userId1, validFilename, file));
    }

    @Test
    void delete() {
        when(fileRepository.findByUseridAndFilename(userId1, fileEntity.getFilename())).thenReturn(fileEntity);
        doNothing().when(fileRepository).removeByUseridAndFilename(userId1, fileEntity.getFilename());

        fileService.delete(userId1, fileEntity.getFilename());
    }

    @Test
    void delete_fileNotFound() {
        assertThrows(FileNotFound.class, () -> fileService.delete(userId1, validFilename));
    }

    @Test
    void delete_invalidFileName() {
        assertThrows(ErrorInputData.class, () -> fileService.delete(userId1, invalidStr));
    }

    @Test
    void editFileName() {
        String newName = "newName";
        when(fileRepository.findByUseridAndFilename(userId1, fileEntity.getFilename())).thenReturn(fileEntity);
        doNothing().when(fileRepository).updateFileNameByUserId(userId1, fileEntity.getFilename(), newName);
        fileService.editFileName(userId1, fileEntity.getFilename(), new FileNameEditRequest(newName));
        verify(fileRepository, times(1)).updateFileNameByUserId(userId1, fileEntity.getFilename(), newName);
    }

    @Test
    void editFileName_invalidFileName() {
        assertThrows(ErrorInputData.class, () -> fileService.editFileName(userId1, invalidStr, new FileNameEditRequest(validFilename)));
    }

    @Test
    void editFileName_invalidNewFileName() {
        assertThrows(ErrorInputData.class, () -> fileService.editFileName(userId1, validFilename, new FileNameEditRequest(invalidStr)));
    }

    @Test
    void editFileName_fileNotFound() {
        assertThrows(FileNotFound.class, () -> fileService.editFileName(userId1, validFilename, new FileNameEditRequest(validFilename)));
    }

    @Test
    void download() {
        when(fileRepository.findByUseridAndFilename(userId1, fileEntity.getFilename())).thenReturn(fileEntity);

        Files download = fileService.download(userId1, fileEntity.getFilename());
        assertEquals(fileEntity, download);
    }

    @Test
    void download_fileNotFound() {
        assertThrows(FileNotFound.class, () -> fileService.download(userId1, validFilename));
    }

    @Test
    void getAllFiles() {
        List<Files> files = new ArrayList<>();
        files.add(fileEntity);
        when(fileRepository.findAllByUseridWithLimit(userId1, 100)).thenReturn(files);

        List<FileDTO> allFiles = fileService.getAllFiles(userId1, 100);
        assertEquals(files.size(), allFiles.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void getAllFiles_invalidLimit(int param) {
        assertThrows(ErrorInputData.class, () -> fileService.getAllFiles(userId1, param));
    }
}