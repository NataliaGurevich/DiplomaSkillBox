package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostUtil;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceMyPost {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostUtil postUtil;

    @Autowired
    public PostServiceMyPost(PostRepository postRepository, PostCommentRepository postCommentRepository, PostVoteRepository postVoteRepository, PostUtil postUtil) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.postVoteRepository = postVoteRepository;
        this.postUtil = postUtil;
    }

    public ResponseEntity<PostsResponse> getMyPosts(int offset, int limit, String status, User currentUser) {

        final String INACTIVE_STATUS = "INACTIVE";
        final String PENDING_STATUS = "PENDING";
        final String DECLINED_STATUS = "DECLINED";
        final String PUBLISHED_STATUS = "PUBLISHED";

        int currentPage = offset / limit;
        Pageable paging = PageRequest.of(currentPage, limit);
        List<PostResponse> posts = new ArrayList<>();
        Page<Post> postPage;
        List<Post> postList;
        long count = 0;

        if (status.equalsIgnoreCase(INACTIVE_STATUS)) {
            postPage = postRepository.findAllMyPostInactive(currentUser.getId(), paging);
            count = postPage.getTotalElements();

            log.info("POST INACTIVE {}, totalPages {}", postPage, count);

            postList = postPage.stream().collect(Collectors.toList());

            if (postList != null) {
//                posts = cretePostList(postList);
                posts = postUtil.cretePostList(postList);
            }
        } else if (status.equalsIgnoreCase(PENDING_STATUS)) {
            postPage = postRepository.findAllMyPostPending(currentUser.getId(), paging);
            count = postPage.getTotalElements();

            log.info("POST PENDING {}", postPage);

            postList = postPage.stream().collect(Collectors.toList());

            if (postList != null) {
//                posts = cretePostList(postList);
                posts = postUtil.cretePostList(postList);
            }
        } else if (status.equalsIgnoreCase(DECLINED_STATUS)) {
            postPage = postRepository.findAllMyPostDeclined(currentUser.getId(), paging);
            count = postPage.getTotalElements();

            log.info("POST DECLINED {}", postPage);

            postList = postPage.stream().collect(Collectors.toList());

            if (postList != null) {
//                posts = cretePostList(postList);
                posts = postUtil.cretePostList(postList);
            }

        } else if (status.equalsIgnoreCase(PUBLISHED_STATUS)) {
            postPage = postRepository.findAllMyPostPublished(currentUser.getId(), paging);
            count = postPage.getTotalElements();

            log.info("POST PUBLISHED {}", postPage);

            postList = postPage.stream().collect(Collectors.toList());

            if (postList != null) {
//                posts = cretePostList(postList);
                posts = postUtil.cretePostList(postList);
            }
        }
//        PostsResponse postsResponse = getAllPostResponse(count, posts);
        PostsResponse postsResponse = postUtil.getAllPostResponse(count, posts);
        return new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    private List<PostResponse> cretePostList(List<Post> postList) {
        List<PostResponse> posts = new ArrayList<>();

        for (Post post : postList) {
            int likeCount = postVoteRepository.findCountLikes(post.getId()).orElse(0);
            int disLikeCount = postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            int commentCount = postCommentRepository.findCountComments(post.getId()).orElse(0);
            posts.add(PostMapper.converter(post, likeCount, disLikeCount, commentCount));
        }
        return posts;
    }

    public PostsResponse getAllPostResponse(long count, List<PostResponse> posts) {

        PostsResponse postsResponse = new PostsResponse();
        postsResponse.setCount(count);
        postsResponse.setPosts(posts);

        return postsResponse;
    }
}
