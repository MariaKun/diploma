package ru.netology.springmvc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String filename;

    private int size;

    @NotBlank
    private String type;

    @Lob
    @NotNull
    private byte[] filecontent;

    private long userid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File files = (File) o;
        return id == files.id && size == files.size && userid == files.userid && Objects.equals(filename, files.filename) && Objects.equals(type, files.type) && Objects.deepEquals(filecontent, files.filecontent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filename, size, type, Arrays.hashCode(filecontent), userid);
    }
}