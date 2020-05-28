package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.CommentMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.mapper.UserMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.PostComment;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostCommentRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.*;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log
@Data
@Service
@Transactional
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostCommentRepository postCommentRepository;

    private List<PostResponse> getSetPosts() {
        List<PostResponse> posts = new ArrayList<>();
        List<Post> postList = postRepository.findAll();

        if (postList != null) {
            for (Post post : postList) {
                posts.add(PostMapper.converter(post));
            }
        }
        return posts;
    }

    public PostsResponse getAllPost() {
        List<PostResponse> posts = getSetPosts();

        PostsResponse postsResponse = new PostsResponse();
        postsResponse.setCount(posts == null ? 0 : posts.size());
        postsResponse.setPosts(posts);

        log.info("[" + postsResponse.toString() + "]");

        return postsResponse;
    }

    public PostCommentsResponse getPostById(Long id){
        Optional <Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.orElse(null);

        PostCommentsResponse postCommentsResponse = new PostCommentsResponse();
        if (post != null) {
            postCommentsResponse.setId(id);
            postCommentsResponse.setTime(post.getTime());
            postCommentsResponse.setUser(UserMapper.converterToFullName(post.getUser()));
            postCommentsResponse.setTitle(post.getTitle());
            postCommentsResponse.setAnnounce(post.getTitle());
            postCommentsResponse.setLikeCount(1);
            postCommentsResponse.setDislikeCount(1);
            postCommentsResponse.setViewCount(12);

            List<PostComment> comments = postCommentRepository.findAllByPost(post);
            List<CommentResponse> commentsResponses = new ArrayList<>();

            for (PostComment postComment : comments) {
                commentsResponses.add(CommentMapper.converter(postComment));
            }

            postCommentsResponse.setComments(commentsResponses);
            postCommentsResponse.setCommentCount(commentsResponses.size());

            Set<Tag> tagsFromQuery = post.getTags();
            log.info("[tagsPyPost: " + tagsFromQuery.toString() + "]");

            List<String> tags = new ArrayList<>();
            for (Tag tag: tagsFromQuery) {
                tags.add(tag.getName());
            }
            postCommentsResponse.setTags(tags);
        }

        return postCommentsResponse;
    }
}
