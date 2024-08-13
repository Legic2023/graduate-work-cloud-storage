package ru.netology.cloudstorage.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.config.auth.TokenProvider;
import ru.netology.cloudstorage.entities.Storage;
import ru.netology.cloudstorage.entities.User;
import ru.netology.cloudstorage.enums.UserRole;
import ru.netology.cloudstorage.exceptions.AuthMessageException;
import ru.netology.cloudstorage.repositories.StorageRepository;
import ru.netology.cloudstorage.repositories.UsersRepository;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final UsersRepository usersRepository;
    private final StorageRepository storageRepository;
    private final TokenProvider tokenProvider;

    public List<String> getFileList(String authToken) {
        if (isAuthExist(authToken)) {
            return storageRepository.getFileNames();
        } else {
            throw new AuthMessageException();
        }
    }

    public void uploadFile(String authToken, String filename, MultipartFile multipartFile) {
        if (isAuthExist(authToken)) {
            try {
                storageRepository.save(new Storage(filename, multipartFile.getBytes(), getUser()));
            } catch (RuntimeException | IOException e) {
                throw new RuntimeException("File " + filename + ": upload FAILED!");
            }
        } else {
            throw new AuthMessageException();
        }
    }

    public byte[] downloadFile(String authToken, String filename) {
        if (isAuthExist(authToken)) {
            final Storage file = getFileByName(filename);
            return file.getFileContent();
        } else {
            throw new AuthMessageException();
        }
    }

    @Transactional
    public String deleteFile(String authToken, String filename) {
        if (isAuthExist(authToken)) {
            UserRole userRole = getUser().getRole();
            if (userRole == UserRole.ADMIN) {
                if (!storageRepository.existsByFileName(filename)) {
                    throw new RuntimeException("File " + filename + " not found");
                } else {
                    storageRepository.deleteByFileName(filename);
                    return "File " + filename + ": delete SUCCESS!";
                }
            } else {
                return "User role: " + userRole + " is not allowed to delete file";
            }
        } else {
            throw new AuthMessageException();
        }
    }

    public String editFileName(String authToken, String oldFileName, String newFileName) {
        if (isAuthExist(authToken)) {
            try {
                final var storage = getFileByName(oldFileName);
                final var newStorage = new Storage(newFileName, getFileByName(oldFileName).getFileContent(), getUser());
                storageRepository.delete(storage);
                storageRepository.save(newStorage);
                return "SUCCESS edit file: " + oldFileName;
            } catch (RuntimeException e) {
                throw new RuntimeException("ERROR edit file: " + oldFileName);
            }
        } else {
            throw new AuthMessageException();
        }
    }

    private Storage getFileByName(String filename) {
        if (!storageRepository.existsByFileName(filename)) {
            throw new RuntimeException("File " + filename + " not found");
        } else {
            return storageRepository.findByFileName(filename);
        }
    }

    public User getUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();
        var user = usersRepository.findByLogin(username);
        if (user != null) {
            return (User) user;
        } else {
            throw new AuthMessageException();
        }
    }

    public boolean isAuthExist(String authToken) {
        if ((SecurityContextHolder.getContext().getAuthentication() == null)
                || (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated())) {
            return false;
        } else return tokenProvider.validateToken(authToken)
                .equals(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}