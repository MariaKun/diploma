package ru.netology.springmvc.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import ru.netology.springmvc.entity.Files;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class FileRepositoryTest {

    @Autowired
    private FileRepository fileRepository;

    @Test
    void findAllByUseridWithLimit() throws IOException {
        long userId1 = 1000L;
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "text".getBytes());

        Files fileEntity = Files.builder()
                .filecontent(file.getBytes())
                .size(file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(userId1).build();
        fileRepository.save(fileEntity);
        List<Files> allByUseridWithLimit = fileRepository.findAllByUseridWithLimit(userId1, 100);
        assertEquals(1, allByUseridWithLimit.size());
    }

    @Test
    void findByUseridAndFilename_IdNotFound() {
        Files myfile = fileRepository.findByUseridAndFilename(1000, "myfile");
        assertNull(myfile);
    }

    @Test
    void findByUseridAndFilename_FileNotFound() throws IOException {
        long userId1 = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file1", "test.txt", "text/plain", "text".getBytes());

        Files fileEntity = Files.builder()
                .filecontent(file.getBytes())
                .size(file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(userId1).build();
        fileRepository.save(fileEntity);
        Files myfile = fileRepository.findByUseridAndFilename(1L, "myfile1000");
        assertNull(myfile);
    }

    @Test
    void updateFileNameByUserId() throws IOException {
        long userId1 = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file1", "test.txt", "text/plain", "text".getBytes());

        Files fileEntity = Files.builder()
                .filecontent(file.getBytes())
                .size(file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(userId1).build();
        fileRepository.save(fileEntity);

        fileRepository.updateFileNameByUserId(userId1, fileEntity.getFilename(), "newName");
        Files newName = fileRepository.findByUseridAndFilename(userId1, "newName");
        assertEquals(fileEntity.getId(), newName.getId());

        Files oldName = fileRepository.findByUseridAndFilename(userId1, fileEntity.getFilename());
        assertNull(oldName);
    }

    @Test
    void findByUseridAndFilename() throws IOException {
        long userId1 = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file1", "test.txt", "text/plain", "text".getBytes());

        Files fileEntity = Files.builder()
                .filecontent(file.getBytes())
                .size(file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(userId1).build();
        fileRepository.save(fileEntity);

        Files byUseridAndFilename = fileRepository.findByUseridAndFilename(userId1, file.getName());

        assertEquals(file.getName(), byUseridAndFilename.getFilename());
        assertEquals(file.getSize(), byUseridAndFilename.getSize());
        assertEquals(file.getContentType(), byUseridAndFilename.getType());
        assertEquals(file.getBytes(), byUseridAndFilename.getFilecontent());
    }
}