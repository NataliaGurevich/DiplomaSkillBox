package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findAll();
    Optional<PostComment> findById(Long id);
    List<PostComment> findAllByPost(Post post);

    @Query(value = "SELECT COUNT(*) FROM post_comments " +
            "WHERE post_id=?1", nativeQuery = true)
    Optional<Integer> findCountComments(Long id);
}
