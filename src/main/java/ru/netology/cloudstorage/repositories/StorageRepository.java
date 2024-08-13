package ru.netology.cloudstorage.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.entities.Storage;

import java.util.List;

@Repository
public interface StorageRepository extends CrudRepository<Storage, Long> {
    @Query(value = "SELECT file FROM db_migration.storage", nativeQuery = true)
    List<String> getFileNames();

    boolean existsByFileName(String fileName);

    void deleteByFileName(String fileName);

    Storage findByFileName(String fileName);
}