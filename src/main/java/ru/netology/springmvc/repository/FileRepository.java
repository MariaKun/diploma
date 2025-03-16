package ru.netology.springmvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.springmvc.entity.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    void removeByUseridAndFilename(long userid, String filename);

    @Query(value = "select * from files f where f.userid = ?1 limit ?2", nativeQuery = true)
    List<File> findAllByUseridWithLimit(long userid, int limit);

    @Modifying(clearAutomatically = true)
    @Query("update File f set f.filename = :newName where f.filename = :fileName and f.userid = :userid")
    void updateFileNameByUserId(@Param("userid") long userid, @Param("fileName") String oldFileName, @Param("newName") String newFileName);

    File findByUseridAndFilename(long userid, String filename);
}
