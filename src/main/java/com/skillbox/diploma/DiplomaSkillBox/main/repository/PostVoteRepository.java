package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Long> {
    List<PostVote> findAll();
    Optional<PostVote> findById(Long id);

    @Query(value = "SELECT COUNT(*) FROM post_votes " +
            "WHERE post_id=?1 and value=true", nativeQuery = true)
    Optional<Integer> findCountLikes(Long id);

    @Query(value = "SELECT COUNT(*) FROM post_votes " +
            "WHERE post_id=?1 and value=false", nativeQuery = true)
    Optional<Integer> findCountDislikes(Long id);

    @Query(value = "SELECT * FROM post_votes WHERE post_id=?1 and user_id=?2", nativeQuery = true)
    Optional<PostVote> findByPostAndUser(Long postId, Long userId);
}
