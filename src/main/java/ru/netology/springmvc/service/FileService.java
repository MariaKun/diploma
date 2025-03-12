package ru.netology.springmvc.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.springmvc.entity.Files;
import ru.netology.springmvc.exception.*;
import ru.netology.springmvc.model.FileDTO;
import ru.netology.springmvc.model.FileNameEditRequest;
import ru.netology.springmvc.repository.FileRepository;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Transactional
    public Files upload(long userid, String filename, MultipartFile file) {
        if (isInvalid(filename)) {
            throw new ErrorInputData(String.format("Invalid filename %s", filename));
        }
        if (file.getSize() > 5242880) {
            throw new ErrorInputData(String.format("File %s greater than 5MB", filename));
        }
        try {
            if (fileRepository.findByUseridAndFilename(userid, filename) != null) {
                throw new ErrorInputData(String.format("File already exist %s", filename));
            }
            Files fileEntity = Files.builder()
                    .filecontent(file.getBytes())
                    .size(file.getSize())
                    .filename(filename)
                    .type(file.getContentType())
                    .userid(userid).build();
            Files save = fileRepository.save(fileEntity);
            log.info("File {} success uploaded for user {}", filename, userid);
            return save;
        } catch (IOException ex) {
            throw new ErrorUploadFile(String.format("Error upload file %s : %s", filename, ex.getMessage()));
        }
    }

    @Transactional
    public void delete(long userid, String filename) {
        if (isInvalid(filename)) {
            throw new ErrorInputData(String.format("Invalid filename %s", filename));
        }
        if (fileRepository.findByUseridAndFilename(userid, filename) == null) {
            throw new FileNotFound(String.format("File %s not found", filename));
        }
        try {
            fileRepository.removeByUseridAndFilename(userid, filename);
            log.info("File {} success deleted", filename);
        } catch (Exception ex) {
            throw new ErrorDeleteFile(String.format("Error delete file %s : %s", filename, ex.getMessage()));
        }
    }

    @Transactional
    public void editFileName(long userid, String filename, FileNameEditRequest fileNameEditRequest) {
        if (isInvalid(filename)) {
            throw new ErrorInputData(String.format("Invalid filename %s", filename));
        }
        if (isInvalid(fileNameEditRequest.getFilename())) {
            throw new ErrorInputData(String.format("Invalid new filename %s", fileNameEditRequest.getFilename()));
        }
        if (fileRepository.findByUseridAndFilename(userid, filename) == null) {
            throw new FileNotFound(String.format("File %s not found", filename));
        }
        if (fileRepository.findByUseridAndFilename(userid, fileNameEditRequest.getFilename()) != null) {
            throw new ErrorInputData(String.format("File already exist %s", fileNameEditRequest.getFilename()));
        }
        try {
            fileRepository.updateFileNameByUserId(userid, filename, fileNameEditRequest.getFilename());
            log.info("File name success changed from {} to {}", filename, fileNameEditRequest.getFilename());
        } catch (Exception ex) {
            throw new ErrorUploadFile(String.format("Error change filename from %s to %s : %s", filename, fileNameEditRequest.getFilename(), ex.getMessage()));
        }
    }

    @Transactional
    public Files download(long userid, String filename) {
        if (fileRepository.findByUseridAndFilename(userid, filename) == null) {
            throw new FileNotFound(String.format("File %s not found", filename));
        }
        try {
            Files files = fileRepository.findByUseridAndFilename(userid, filename);
            log.info("File {} success downloaded", filename);
            return files;
        } catch (Exception ex) {
            throw new ErrorDownloadFile(String.format("Error download file %s : %s", filename, ex.getMessage()));
        }
    }

    @Transactional
    public List<FileDTO> getAllFiles(long userid, int limit) {
        if (limit < 1)
            throw new ErrorInputData(String.format("Invalid limit %s", limit));
        try {
            List<FileDTO> files = fileRepository.findAllByUseridWithLimit(userid, limit)
                    .stream().map(x -> new FileDTO(x.getFilename(), x.getSize())).toList();
            log.info("Success get list for user {}", userid);
            return files;
        } catch (Exception ex) {
            throw new ErrorGettingFileList(String.format("Error getting file list for user %s : %s", userid, ex.getMessage()));
        }
    }

    private boolean isInvalid(String str) {
        return str.isEmpty();
    }
}
