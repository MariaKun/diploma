package ru.netology.springmvc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private String type;

    @Lob
    @Column(nullable = false)
    private byte[] filecontent;

    private long userid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Files files = (Files) o;
        return id == files.id && size == files.size && userid == files.userid && Objects.equals(filename, files.filename) && Objects.equals(type, files.type) && Objects.deepEquals(filecontent, files.filecontent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filename, size, type, Arrays.hashCode(filecontent), userid);
    }
}