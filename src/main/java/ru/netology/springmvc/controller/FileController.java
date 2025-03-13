package ru.netology.springmvc.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.springmvc.entity.Files;
import ru.netology.springmvc.model.FileNameEditRequest;
import ru.netology.springmvc.service.FileService;
import ru.netology.springmvc.service.UserService;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("filename") String filename, MultipartFile file) throws IOException {
        fileService.upload(userService.getCurrentUser().getId(), filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String filename) {

        fileService.delete(userService.getCurrentUser().getId(), filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<byte[]> downloadFile(@RequestParam("filename") String filename) {
        Files file = fileService.download(userService.getCurrentUser().getId(), filename);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getFilecontent());
    }

    @PutMapping
    public ResponseEntity<?> editFileName(@RequestParam("filename") String filename,
                                          @RequestBody FileNameEditRequest fileNameEditRequest) {
        fileService.editFileName(userService.getCurrentUser().getId(), filename, fileNameEditRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
