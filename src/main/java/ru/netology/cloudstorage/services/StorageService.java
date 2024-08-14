package ru.netology.cloudstorage.services;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entities.Storage;
import ru.netology.cloudstorage.entities.User;
import ru.netology.cloudstorage.enums.UserRole;

import java.io.IOException;
import java.util.List;

public interface StorageService {

    List<String> getFileList();

    void uploadFile(String filename, MultipartFile multipartFile) throws IOException;

    byte[] downloadFile(User user, String filename);

    String deleteFile(User user, String filename);

    String editFileName(User user, String oldFileName, String newFileName);

    void saveUploadFile(String filename, MultipartFile multipartFile);
}
