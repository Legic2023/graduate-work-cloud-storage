package ru.netology.cloudstorage.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dtos.FileNameInRequest;
import ru.netology.cloudstorage.services.StorageServiceImpl;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class StorageController {

    @Autowired
    private final StorageServiceImpl storageServiceImpl;

    @GetMapping("/list")
    public ResponseEntity<List<String>> getFileList(@RequestHeader("auth-token") @NotBlank String authToken) {
        return ResponseEntity.ok(storageServiceImpl.getFileList(authToken));
    }

    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") @NotBlank String authToken, @NotBlank String filename,
                                        @RequestBody MultipartFile multipartFile) {
        storageServiceImpl.uploadFile(authToken, filename, multipartFile);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(value = "/files", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestHeader("auth-token") @NotBlank String authToken, @NotBlank String filename) {
        return ResponseEntity.ok().body(new ByteArrayResource(storageServiceImpl.downloadFile(authToken, filename)));
    }

    @DeleteMapping("/files")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") @NotBlank String authToken, @NotBlank String filename) {
        return ResponseEntity.ok(storageServiceImpl.deleteFile(authToken, filename));
    }

    @PutMapping(value = "/files")
    public ResponseEntity<String> editFileName(@RequestHeader("auth-token") @NotBlank String authToken, @NotBlank String filename,
                                               @Valid @RequestBody FileNameInRequest editFileName) {
        return ResponseEntity.ok(storageServiceImpl.editFileName(authToken, filename, editFileName.getFilename()));
    }

}





