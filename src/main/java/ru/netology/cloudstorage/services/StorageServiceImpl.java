package ru.netology.cloudstorage.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entities.Storage;
import ru.netology.cloudstorage.entities.User;
import ru.netology.cloudstorage.enums.UserRole;
import ru.netology.cloudstorage.exceptions.InvalidAuthException;
import ru.netology.cloudstorage.exceptions.InvalidEditFile;
import ru.netology.cloudstorage.exceptions.InvalidFindFile;
import ru.netology.cloudstorage.exceptions.InvalidUploadFile;
import ru.netology.cloudstorage.repositories.StorageRepository;
import ru.netology.cloudstorage.repositories.UsersRepository;

import java.io.IOException;
import java.util.List;

import static ru.netology.cloudstorage.enums.UserRole.ADMIN;

@Slf4j
@Service
@AllArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final UsersRepository usersRepository;
    private final StorageRepository storageRepository;

    public List<String> getFileList() {
        log.debug("getFileList");
        return storageRepository.getFileNames();
    }

    public void uploadFile(String filename, MultipartFile multipartFile) {
        log.debug("uploadFile");
        saveUploadFile(filename, multipartFile);
    }

    public byte[] downloadFile(User user, String filename) {
        final Storage file = getFileByNameAndUser(filename, user);
        log.debug("downloadFile");
        return file.getFileContent();
    }

    @Transactional
    public String deleteFile(User user, String filename) {
        log.debug("deleteFile");
        if (!checkUserRole(user.getRole())) {
            return "User role: " + user.getRole() + " is not allowed to delete file";
        }
        if (storageRepository.existsByFileNameAndUser(filename, user)) {
            storageRepository.deleteByFileNameAndUser(filename, user);
            return "File " + filename + ": delete SUCCESS!";
        }
        throw new InvalidFindFile();
    }

    public String editFileName(User user, String oldFileName, String newFileName) {
        log.debug("editFileName");
        try {
            final var storage = getFileByNameAndUser(oldFileName, user);
            final var newStorage = new Storage(newFileName, getFileByNameAndUser(oldFileName, user).getFileContent(), getUser());
            storageRepository.delete(storage);
            storageRepository.save(newStorage);
            return "SUCCESS edit file: " + oldFileName;
        } catch (InvalidEditFile e) {
            throw new InvalidEditFile();
        }
    }

    private Storage getFileByNameAndUser(String filename, User user) {
        log.debug("getFileByName");
        if (storageRepository.existsByFileNameAndUser(filename, user)) {
            return storageRepository.findByFileNameAndUser(filename, user);
        }
        throw new InvalidFindFile();
    }

    public User getUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();
        var user = usersRepository.findByLogin(username);
        if (user == null) {
            throw new InvalidAuthException();
        }
        log.debug("getUser: {}", user.getUsername());
        return (User) user;
    }

    private boolean checkUserRole(UserRole userRole) {
        return userRole == ADMIN;
    }

    public void saveUploadFile(String filename, MultipartFile multipartFile) {
        try {
            storageRepository.save(new Storage(filename, multipartFile.getBytes(), getUser()));
        } catch (InvalidUploadFile | IOException e) {
            log.error("Error: ", e);
        }

    }
}