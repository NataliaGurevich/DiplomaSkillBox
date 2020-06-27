package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.PostComment;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.*;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostByTagResponse;
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
public class PostServiceByTag {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final TagToPostRepository tagToPostRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostServiceByTag(TagRepository tagRepository, PostRepository postRepository, TagToPostRepository tagToPostRepository, PostVoteRepository postVoteRepository, PostCommentRepository postCommentRepository, PostMapper postMapper) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;
        this.postMapper = postMapper;
    }

    public PostsResponse getSetPosts(int offset, int limit, String tagName) {

        int currentPage = offset / limit;
        Pageable paging = PageRequest.of(currentPage, limit);
        List<PostByTagResponse> posts = new ArrayList<>();
        Page<Post> postPage;
        List<Post> postList;
        long count;

        Tag tag = tagRepository.findTagByName(tagName).orElse(null);

        if (tag != null) {
            postPage = tagToPostRepository.findPostByTag(tag, paging);
            count = postPage.stream().filter(p -> p.getIsActive()
                    && p.getModerationStatus().equalsIgnoreCase("ACCEPTED")
                    && p.getTime().isBefore(Instant.now())).count();

            log.info("TAG {} -> post count {}", tag.getName(), count);

            postList = postPage.stream().filter(p -> p.getIsActive()
                    && p.getModerationStatus().equalsIgnoreCase("ACCEPTED")
                    && p.getTime().isBefore(Instant.now())).collect(Collectors.toList());

            if (postList.size() > 0) {
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

    private List<PostByTagResponse> cretePostList(List<Post> postList) {
        List<PostByTagResponse> posts = new ArrayList<>();

        for (Post post : postList) {
            int likeCount = postVoteRepository.findCountLikes(post.getId()).orElse(0);
            int disLikeCount = postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            int commentCount = postCommentRepository.findCountComments(post.getId()).orElse(0);
            List<PostComment> postComments = postCommentRepository.findAllByPost(post);

            posts.add(postMapper.converterPostByTag(post, likeCount, disLikeCount, commentCount, postComments));
        }
        return posts;
    }

    public PostsResponse getAllPostResponse(long count, List<PostByTagResponse> posts) {

        PostsResponse postsResponse = new PostsResponse();
        postsResponse.setCount(count);
        postsResponse.setPosts(posts);

        log.info("POSTS {}", postsResponse);

        return postsResponse;
    }
}
