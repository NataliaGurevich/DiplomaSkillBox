package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAll();

    @Query(value = "SELECT * FROM tags WHERE name=?1", nativeQuery = true)
    Tag findIdByName(String name);

    @Query(value = "SELECT id FROM tags WHERE name=?1", nativeQuery = true)
    Long findIdByTagName(String tagName);

    @Query(value = "SELECT name FROM tags", nativeQuery = true)
    List<String> findAllTagName();

    @Query(value = "SELECT * FROM tags WHERE name=?1", nativeQuery = true)
    Optional<Tag> findTagByName(String tagName);
}
