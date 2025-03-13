package ru.netology.springmvc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class FileDTO {
    private String filename;
    private int size;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDTO fileDTO = (FileDTO) o;
        return size == fileDTO.size && Objects.equals(filename, fileDTO.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, size);
    }
}
