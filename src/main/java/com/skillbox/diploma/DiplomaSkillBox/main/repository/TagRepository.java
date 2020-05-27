package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAll();
}
