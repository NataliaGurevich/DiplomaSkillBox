package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
    Page<Post> findAll(Pageable paging);

    Optional<Post> findById(Long id);

    @Query(value = "SELECT * FROM posts " +
            "WHERE is_active=true and moderation_status='ACCEPTED' and time<=?1 " +
            "ORDER BY time DESC", nativeQuery = true)
    Page<Post> findAllPostRecent(Instant instant, Pageable paging);

    @Query(value = "SELECT * FROM posts " +
            "WHERE is_active=true and moderation_status='ACCEPTED' and time<=?1 " +
            "ORDER BY time ASC", nativeQuery = true)
    Page<Post> findAllPostEarly(Instant instant, Pageable paging);

    @Query(value = "SELECT * FROM posts " +
            "WHERE is_active=true and moderation_status='ACCEPTED' and time=?1 ", nativeQuery = true)
    Page<Post> findAllPostByDate(Instant instant, Pageable paging);

    @Query(value = "SELECT COUNT(*) FROM posts " +
            "WHERE is_active=true and moderation_status='ACCEPTED' and time<=?1", nativeQuery = true)
    Optional<Integer> findCountPosts(Instant instant);

    @Query(value = "SELECT * FROM posts WHERE is_active=true and moderation_status=?1", nativeQuery = true)
    Page<Post> findPostForModeration(String status, Pageable paging);

    @Query(value = "SELECT * FROM posts WHERE is_active=true", nativeQuery = true)
    List<Post> findPostForModerationList();

    @Query(value = "SELECT COUNT(*) FROM posts WHERE is_active=true and moderation_status='NEW'", nativeQuery = true)
    Optional<Integer> findNewPosts();

    @Query(value = "SELECT time FROM posts WHERE is_active=true and moderation_status='ACCEPTED' and time<=?1"
            , nativeQuery = true)
    Optional<List<Date>> findListDates(Instant instant);

    @Query(value = "SELECT COUNT(*) FROM posts " +
            "WHERE is_active=true and moderation_status='ACCEPTED' and TEXT(time) like ?1", nativeQuery = true)
    Optional<Integer> findCountPostsForCalendar(String day);

    @Query(value = "SELECT * FROM posts " +
            "WHERE is_active=true and moderation_status='ACCEPTED' and time<=?1 and TEXT(time) like ?2", nativeQuery = true)
    Page<Post> findPostsByDate(Instant instant, String day, Pageable paging);
}
