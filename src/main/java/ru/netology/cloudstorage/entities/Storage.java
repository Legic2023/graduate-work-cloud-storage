package ru.netology.cloudstorage.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Table(name = "storage")
@Entity
@Getter
@NoArgsConstructor
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "file")
    private String fileName;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "data")
    private byte[] fileContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Storage(String fileName, byte[] fileContent, User user) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.user = user;
    }

}
