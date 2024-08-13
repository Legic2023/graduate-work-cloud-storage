package ru.netology.cloudstorage.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    List<String> getFileList(String authToken);

    void uploadFile(String authToken, String filename, MultipartFile multipartFile);

    byte[] downloadFile(String authToken, String filename);

    String deleteFile(String authToken, String filename);

    String editFileName(String authToken, String oldFileName, String newFileName);


}
