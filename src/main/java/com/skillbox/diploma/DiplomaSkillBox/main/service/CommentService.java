package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.PostComment;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostCommentRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.CommentRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.IdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class CommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(PostCommentRepository postCommentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postCommentRepository = postCommentRepository;
        this.postRepository = postRepository;
    }

    public IdResponse addComment(CommentRequest commentRequest, User currentUser) {

        Long postId = commentRequest.getPostId();
        Long parentId = commentRequest.getParentId();
        String text = commentRequest.getText();

        PostComment postComment = new PostComment();
        postComment.setPost(postRepository.findById(postId).orElse(null));
        if (parentId != null){
            postComment.setParent(postCommentRepository.findById(parentId).orElse(null));
        }
        postComment.setUser(currentUser);
        postComment.setText(text);
        postComment.setTime(Instant.now());

        PostComment savedComment = postCommentRepository.save(postComment);
        return new IdResponse(savedComment.getId());
    }
}
