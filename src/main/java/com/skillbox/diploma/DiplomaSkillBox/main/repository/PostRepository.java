package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAll();
    Optional<Post> findById(Long id);
}
