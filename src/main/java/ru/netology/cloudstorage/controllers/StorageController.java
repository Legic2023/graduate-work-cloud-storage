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
import ru.netology.cloudstorage.config.auth.TokenProvider;
import ru.netology.cloudstorage.dtos.FileNameInRequest;
import ru.netology.cloudstorage.entities.User;
import ru.netology.cloudstorage.exceptions.InvalidAuthException;
import ru.netology.cloudstorage.services.StorageServiceImpl;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class StorageController {

    @Autowired
    private final StorageServiceImpl storageServiceImpl;

    @Autowired
    private final TokenProvider tokenProvider;

    @GetMapping("/list")
    public ResponseEntity<List<String>> getFileList(@RequestHeader("auth-token") @NotBlank String authToken) {
        if (tokenProvider.getCurrentUser(authToken).isEnabled()) {
            return ResponseEntity.ok(storageServiceImpl.getFileList());
        }
        throw new InvalidAuthException();
    }

    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") @NotBlank String authToken, @NotBlank String filename,
                                        @RequestBody MultipartFile multipartFile) {
        if (tokenProvider.getCurrentUser(authToken).isEnabled()) {
            storageServiceImpl.uploadFile(filename, multipartFile);
            return ResponseEntity.ok(HttpStatus.OK);
        }
        throw new InvalidAuthException();
    }

    @GetMapping(value = "/files", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestHeader("auth-token") @NotBlank String authToken, @NotBlank String filename) {
        if (tokenProvider.getCurrentUser(authToken).isEnabled()) {
            User user = tokenProvider.getCurrentUser(authToken);
            return ResponseEntity.ok().body(new ByteArrayResource(storageServiceImpl.downloadFile(user, filename)));
        }
        throw new InvalidAuthException();
    }


    @DeleteMapping("/files")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") @NotBlank String authToken, @NotBlank String filename) {
        if (tokenProvider.getCurrentUser(authToken).isEnabled()) {
            User user = tokenProvider.getCurrentUser(authToken);
            return ResponseEntity.ok(storageServiceImpl.deleteFile(user, filename));
        }
        throw new InvalidAuthException();
    }


    @PutMapping(value = "/files")
    public ResponseEntity<String> editFileName(@RequestHeader("auth-token") @NotBlank String
                                                       authToken, @NotBlank String filename,
                                               @Valid @RequestBody FileNameInRequest editFileName) {
        if (tokenProvider.getCurrentUser(authToken).isEnabled()) {
            User user = tokenProvider.getCurrentUser(authToken);
            return ResponseEntity.ok(storageServiceImpl.editFileName(user, filename, editFileName.getFilename()));
        }
        throw new InvalidAuthException();
    }
}




