package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.PostComment;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostCommentRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostVoteRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.TagToPostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostCommentsResponse;
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
public class PostServiceByMode {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private TagToPostRepository tagToPostRepository;


    public PostsResponse getSetPosts(int offset, int limit, String mode) {

        int currentPage = offset / limit;
        Pageable paging = PageRequest.of(currentPage, limit);
        List<PostResponse> posts = new ArrayList<>();
        Page<Post> postPage;
        List<Post> postList;
        long count = 0;

        if (mode.equalsIgnoreCase("recent")) {
            postPage = postRepository.findAllPostRecent(Instant.now(), paging);
            count = postPage.getTotalElements();

            log.info("POST RECENT {}, totalPages {}", postPage, count);

            postList = postPage.stream().collect(Collectors.toList());

            if (postList.size() > 0) {
                posts = cretePostList(postList);
            }
        } else if (mode.equalsIgnoreCase("early")) {
            postPage = postRepository.findAllPostEarly(Instant.now(), paging);
            count = postPage.getTotalElements();

            log.info("POST EARLY {}", postPage);

            postList = postPage.stream().collect(Collectors.toList());

            if (postList != null) {
                posts = cretePostList(postList);
            }
        } else if (mode.equalsIgnoreCase("popular")) {
            postPage = postRepository.findAllPostEarly(Instant.now(), paging);

            postList = postPage.stream().collect(Collectors.toList());

            if (postList != null) {

                count = postList.stream().peek(p -> {
                    Integer countComment = postCommentRepository.findCountComments(p.getId()).orElse(0);
                    p.setCommentCount(countComment);
                }).count();

                postList = postList.stream().peek(p -> {
                            Integer countComment = postCommentRepository.findCountComments(p.getId()).orElse(0);
                            p.setCommentCount(countComment);
                        }
                ).collect(Collectors.toList());

                postList = postList.stream().sorted((p1, p2) -> p2.getCommentCount()
                        .compareTo(p1.getCommentCount())).collect(Collectors.toList());

                posts = cretePostList(postList);
            }
        } else if (mode.equalsIgnoreCase("best")) {
            postPage = postRepository.findAllPostEarly(Instant.now(), paging);

            postList = postPage.stream().collect(Collectors.toList());

            if (postList != null) {

                count = postList.stream().peek(p -> {
                    Integer countLike = postVoteRepository.findCountLikes(p.getId()).orElse(0);
                    p.setLikeCount(countLike);
                }).count();

                postList = postList.stream().peek(p -> {
                            Integer countLike = postVoteRepository.findCountLikes(p.getId()).orElse(0);
                            p.setLikeCount(countLike);
                        }
                ).collect(Collectors.toList());

                postList = postList.stream().sorted((p1, p2) -> p2.getLikeCount().compareTo(p1.getLikeCount())).collect(Collectors.toList());
                posts = cretePostList(postList);
            }
        } else {
            postPage = postRepository.findAll(paging);
            count = postPage.getSize();

            postList = postPage.stream().collect(Collectors.toList());

            log.info("POST ALL {}", postList);

            if (postList != null) {
                posts = cretePostList(postList);
            }
        }
        PostsResponse postsResponse = getAllPostResponse(count, posts);
        return postsResponse;
    }

    public PostsResponse getAllPostByDate(int offset, int limit, Instant instant) {

        int currentPage = offset / limit;
        Pageable paging = PageRequest.of(currentPage, limit);
        Page<Post> postPage = postRepository.findAllPostByDate(instant, paging);
        long count = postPage.getTotalElements();
        List<Post> postList = postPage.stream().collect(Collectors.toList());
        List<PostResponse> posts = new ArrayList<>();

        if (postList != null) {
            posts = cretePostList(postList);
        }
        PostsResponse postsResponse = getAllPostResponse(count, posts);
        return postsResponse;
    }

    public PostCommentsResponse getPostById(Long id, User currentUser) {

        Post post = postRepository.findById(id).orElse(null);
        PostCommentsResponse postCommentsResponse = null;

        if (post != null) {
            int likeCount = postVoteRepository.findCountLikes(post.getId()).orElse(0);
            int disLikeCount = postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            int commentCount = postCommentRepository.findCountComments(post.getId()).orElse(0);

            if (!currentUser.getId().equals(post.getUser().getId())) {
                int viewCount = post.getViewCount() == null ? 1 : post.getViewCount() + 1;
                post.setViewCount(viewCount);
                postRepository.save(post);
            }

            List<PostComment> postComments = postCommentRepository.findAllByPost(post);

            List<Tag> tags = tagToPostRepository.findByPost(post);
            List<String> tagsName = tags.stream().map(t -> t.getName()).collect(Collectors.toList());

            postCommentsResponse = PostMapper.converterPostWithComment(post, likeCount,
                    disLikeCount, commentCount, postComments, tagsName);
        }

        return postCommentsResponse;
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
}
