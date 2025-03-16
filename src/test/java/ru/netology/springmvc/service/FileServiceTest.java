package ru.netology.springmvc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.netology.springmvc.entity.File;
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
import static ru.netology.springmvc.TestData.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    FileService fileService;

    @Mock
    FileRepository fileRepository;

    private final MockMultipartFile file = randomFile();

    private final File fileEntity = File.builder()
            .filecontent(file.getBytes())
            .size((int) file.getSize())
            .filename(file.getName())
            .type(file.getContentType())
            .userid(testUserId).build();

    FileServiceTest() throws IOException {
    }

    @Test
    void upload() {
        when(fileRepository.save(fileEntity)).thenReturn(fileEntity);

        File upload = fileService.upload(testUserId, file.getName(), file);
        assertEquals(fileEntity, upload);
        verify(fileRepository, times(1)).save(fileEntity);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void upload_emptyFileName(String filename) {
        assertThrows(ErrorInputData.class, () -> fileService.upload(testUserId, filename, file));
    }

    @Test
    void upload_fileExist() {
        when(fileRepository.findByUseridAndFilename(testUserId, file.getName())).thenReturn(fileEntity);

        assertThrows(ErrorInputData.class, () -> fileService.upload(testUserId, file.getName(), file));
        verify(fileRepository, times(1)).findByUseridAndFilename(testUserId, file.getName());
    }

    @Test
    void delete() {
        when(fileRepository.findByUseridAndFilename(testUserId, fileEntity.getFilename())).thenReturn(fileEntity);
        doNothing().when(fileRepository).removeByUseridAndFilename(testUserId, fileEntity.getFilename());

        fileService.delete(testUserId, fileEntity.getFilename());
        verify(fileRepository, times(1)).removeByUseridAndFilename(testUserId, fileEntity.getFilename());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void delete_emptyFileName(String filename) {
        assertThrows(ErrorInputData.class, () -> fileService.delete(testUserId, filename));
    }

    @Test
    void delete_fileNotFound() {
        assertThrows(FileNotFound.class, () -> fileService.delete(testUserId, file.getName()));
    }

    @Test
    void editFileName() {
        String newName = randomFileName();
        when(fileRepository.findByUseridAndFilename(testUserId, fileEntity.getFilename())).thenReturn(fileEntity);
        doNothing().when(fileRepository).updateFileNameByUserId(testUserId, fileEntity.getFilename(), newName);

        fileService.editFileName(testUserId, fileEntity.getFilename(), new FileNameEditRequest(newName));
        verify(fileRepository, times(1)).updateFileNameByUserId(testUserId, fileEntity.getFilename(), newName);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void editFileName_emptyFileName(String filename) {
        assertThrows(ErrorInputData.class, () -> fileService.editFileName(testUserId, filename, new FileNameEditRequest(file.getName())));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void editFileName_emptyNewFileName(String filename) {
        assertThrows(ErrorInputData.class, () -> fileService.editFileName(testUserId, file.getName(), new FileNameEditRequest(filename)));
    }

    @Test
    void editFileName_fileNotFound() {
        assertThrows(FileNotFound.class, () -> fileService.editFileName(testUserId, file.getName(), new FileNameEditRequest(file.getName())));
    }

    @Test
    void download() {
        when(fileRepository.findByUseridAndFilename(testUserId, fileEntity.getFilename())).thenReturn(fileEntity);

        File download = fileService.download(testUserId, fileEntity.getFilename());
        assertEquals(fileEntity, download);
        verify(fileRepository, times(1)).findByUseridAndFilename(testUserId, fileEntity.getFilename());
    }

    @Test
    void download_fileNotFound() {
        when(fileRepository.findByUseridAndFilename(testUserId, file.getName())).thenReturn(null);

        assertThrows(FileNotFound.class, () -> fileService.download(testUserId, file.getName()));
        verify(fileRepository, times(1)).findByUseridAndFilename(testUserId, file.getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void download_emptyFileName(String filename) {
        assertThrows(ErrorInputData.class, () -> fileService.download(testUserId, filename));
    }

    @Test
    void getAllFiles() {
        List<File> files = new ArrayList<>();
        files.add(fileEntity);
        when(fileRepository.findAllByUseridWithLimit(testUserId, 100)).thenReturn(files);

        List<FileDTO> allFiles = fileService.getAllFiles(testUserId, 100);
        assertEquals(files.size(), allFiles.size());
        verify(fileRepository, times(1)).findAllByUseridWithLimit(testUserId, 100);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void getAllFiles_invalidLimit(int param) {
        assertThrows(ErrorInputData.class, () -> fileService.getAllFiles(testUserId, param));
    }
}