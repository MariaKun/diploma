package ru.netology.springmvc.repository;


import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import ru.netology.springmvc.entity.File;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.springmvc.TestData.*;

@DataJpaTest
class FileRepositoryTest {

    private final MockMultipartFile file = randomFile();

    @Autowired
    private FileRepository fileRepository;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void save_emptyFileName(String name) throws IOException {
        File fileEntity = File.builder()
                .filecontent(file.getBytes())
                .size((int) file.getSize())
                .filename(name)
                .type(file.getContentType())
                .userid(testUserId).build();
        assertThrows(ConstraintViolationException.class, () -> fileRepository.save(fileEntity));
    }

    @Test
    void save_emptyFileContent() {
        File fileEntity = File.builder()
                .size((int) file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(testUserId).build();
        assertThrows(ConstraintViolationException.class, () -> fileRepository.save(fileEntity));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void save_emptyType(String type) throws IOException {
        File fileEntity = File.builder()
                .size((int) file.getSize())
                .filecontent(file.getBytes())
                .filename(file.getName())
                .type(type)
                .userid(testUserId).build();
        assertThrows(ConstraintViolationException.class, () -> fileRepository.save(fileEntity));
    }

    @Test
    void findAllByUseridWithLimit() throws IOException {
        File fileEntity1 = File.builder()
                .filecontent(file.getBytes())
                .size((int) file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(testUserId).build();

        MockMultipartFile file2 = randomFile();
        File fileEntity2 = File.builder()
                .filecontent(file2.getBytes())
                .size((int) file2.getSize())
                .filename(file2.getName())
                .type(file2.getContentType())
                .userid(testUserId).build();
        //for user1
        fileRepository.save(fileEntity1);
        fileRepository.save(fileEntity2);

        File fileEntity3 = File.builder()
                .filecontent(file2.getBytes())
                .size((int) file2.getSize())
                .filename(file2.getName())
                .type(file2.getContentType())
                .userid(randomUserId()).build();
        //for user2
        fileRepository.save(fileEntity3);

        List<File> allByUseridWithLimit = fileRepository.findAllByUseridWithLimit(testUserId, 100);
        assertEquals(2, allByUseridWithLimit.size());
    }

    @Test
    void findByUseridAndFilename_IdNotFound() throws IOException {
        File fileEntity = File.builder()
                .filecontent(file.getBytes())
                .size((int) file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(testUserId).build();
        fileRepository.save(fileEntity);

        File byUseridAndFilename = fileRepository.findByUseridAndFilename(randomUserId(), file.getName());
        assertNull(byUseridAndFilename);
    }

    @Test
    void findByUseridAndFilename_FileNotFound() throws IOException {
        File fileEntity = File.builder()
                .filecontent(file.getBytes())
                .size((int) file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(testUserId).build();
        fileRepository.save(fileEntity);

        File byUseridAndFilename = fileRepository.findByUseridAndFilename(testUserId, randomFileName());
        assertNull(byUseridAndFilename);
    }

    @Test
    void findByUseridAndFilename() throws IOException {
        File fileEntity = File.builder()
                .filecontent(file.getBytes())
                .size((int) file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(testUserId).build();
        fileRepository.save(fileEntity);

        File byUseridAndFilename = fileRepository.findByUseridAndFilename(testUserId, file.getName());
        assertEquals(fileEntity, byUseridAndFilename);
    }

    @Test
    void updateFileNameByUserId() throws IOException {
        File fileEntity = File.builder()
                .filecontent(file.getBytes())
                .size((int) file.getSize())
                .filename(file.getName())
                .type(file.getContentType())
                .userid(testUserId).build();
        fileRepository.save(fileEntity);

        String newName = randomFileName();
        fileRepository.updateFileNameByUserId(testUserId, fileEntity.getFilename(), newName);

        File fileNewName = fileRepository.findByUseridAndFilename(testUserId, newName);
        assertEquals(fileEntity.getId(), fileNewName.getId());

        File oldName = fileRepository.findByUseridAndFilename(testUserId, fileEntity.getFilename());
        assertNull(oldName);
    }
}