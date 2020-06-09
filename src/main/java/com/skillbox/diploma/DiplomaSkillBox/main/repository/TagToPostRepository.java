package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.model.TagToPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface TagToPostRepository extends PagingAndSortingRepository<TagToPost, Long> {
    List<TagToPost> findAll();

    @Query("SELECT tag FROM TagToPost t WHERE t.post = ?1")
    List<Tag> findByPost(Post post);

    @Query(value = "SELECT COUNT(*) FROM tag2post WHERE tag_id=?1", nativeQuery = true)
    Optional<Integer> findCountPostsProTag(Long id);

    @Query("SELECT post FROM TagToPost t WHERE t.tag=?1")
    List<Post> findPostsProTag(Tag tag);

    @Query("SELECT post FROM TagToPost t WHERE t.tag = ?1")
    Page<Post> findPostByTag(Tag tag, Pageable paging);
}
