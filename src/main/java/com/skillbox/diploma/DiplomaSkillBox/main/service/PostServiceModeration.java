package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostCommentRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostVoteRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.ModerationRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
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
public class PostServiceModeration {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostCommentRepository postCommentRepository;

    @Autowired
    public PostServiceModeration(PostRepository postRepository, PostVoteRepository postVoteRepository, PostCommentRepository postCommentRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;
    }

    public PostsResponse getSetPosts(int offset, int limit, String status, User currentUser) {

        int currentPage = offset / limit;
        Pageable paging = PageRequest.of(currentPage, limit);
        List<PostResponse> posts;
        Page<Post> postPage;
        List<Post> postList;
        long count;

        if (status.equalsIgnoreCase("accepted")) {
            status = "ACCEPTED";
        } else if (status.equalsIgnoreCase("declined")) {
            status = "DECLINED";
        } else {
            status = "NEW";
        }

        if (status.equalsIgnoreCase("ACCEPTED")) {
            postPage = postRepository.findPostForModerationAccepted(currentUser.getId(), paging);
            postList = postPage.stream().collect(Collectors.toList());
            count = postPage.getTotalElements();
        } else {
            postPage = postRepository.findPostForModeration(status, paging);
            postList = postPage.stream().collect(Collectors.toList());
            count = postPage.getTotalElements();
        }

        posts = cretePostList(postList);

        PostsResponse postsResponse = getAllPostResponse(count, posts);
        return postsResponse;
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

        log.info("POSTS {}", postsResponse);

        return postsResponse;
    }

    public ResponseEntity<ResponseBasic> setModeration(ModerationRequest moderationRequest, User moderator) {

        Long postId = moderationRequest.getPostId();
        String moderationStatus = moderationRequest.getDecision().equalsIgnoreCase("accept") ?
                "ACCEPTED" : "DECLINED";

        log.info("MODERATION postId {}, status{}", postId, moderationStatus);

        Post post = postRepository.findById(postId).get();
        post.setModerator(moderator);
        post.setModerationStatus(moderationStatus);
        Post postEdit = postRepository.save(post);

        if (postEdit != null) {
            ResponseBasic responseBasic = ResponseBasic.builder().result(true).build();
            return new ResponseEntity(responseBasic, HttpStatus.OK);
        } else {
            ResponseBasic responseBasic = ResponseBasic.builder().result(false).message("Moderation status don't edit").build();
            return new ResponseEntity(responseBasic, HttpStatus.OK);
        }
    }
}
