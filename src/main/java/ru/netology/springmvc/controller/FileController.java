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

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("filename") String filename, MultipartFile file) throws IOException {
        fileService.upload(1L, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String filename) {

        fileService.delete(1L, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<byte[]> downloadFile(@RequestParam("filename") String filename) {

        Files file = fileService.download(1L, filename);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getFilecontent());
    }

    @PutMapping
    public ResponseEntity<?> editFileName(@RequestParam("filename") String filename,
                                          @RequestBody FileNameEditRequest fileNameEditRequest) {
        fileService.editFileName(1L, filename, fileNameEditRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
