package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostCommentRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostVoteRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceByDate {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostServiceByDate(PostRepository postRepository, PostCommentRepository postCommentRepository, PostVoteRepository postVoteRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.postVoteRepository = postVoteRepository;
        this.postMapper = postMapper;
    }

    public PostsResponse getPostsByDate(int offset, int limit, String day) {

        int currentPage = offset / limit;
        Pageable paging = PageRequest.of(currentPage, limit);
        Page<Post> postPage = postRepository.findPostsByDate(Instant.now(), day, paging);
        long count = postPage.getTotalElements();
        List<Post> postList = postPage.stream().collect(Collectors.toList());
        List<PostResponse> posts = new ArrayList<>();

        if (postList != null) {
            posts = cretePostList(postList);
        }
        PostsResponse postsResponse = getAllPostResponse(count, posts);
        return postsResponse;
    }

    private List<PostResponse> cretePostList(List<Post> postList) {
        List<PostResponse> posts = new ArrayList<>();

        for (Post post : postList) {
            int likeCount = postVoteRepository.findCountLikes(post.getId()).orElse(0);
            int disLikeCount = postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            int commentCount = postCommentRepository.findCountComments(post.getId()).orElse(0);
            posts.add(postMapper.converter(post, likeCount, disLikeCount, commentCount));
        }

        return posts;
    }

    public PostsResponse getAllPostResponse(long count, List<PostResponse> posts) {

        PostsResponse postsResponse = new PostsResponse();
        postsResponse.setCount(count);
        postsResponse.setPosts(posts);

        log.info("POSTS {}", postsResponse);

        return postsResponse;
    }
}
