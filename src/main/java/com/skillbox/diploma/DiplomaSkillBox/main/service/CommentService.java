package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.PostComment;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostCommentRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.CommentRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorMessage;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<ResponseBasic> addComment(CommentRequest commentRequest, User currentUser) {

        Long postId = commentRequest.getPostId();
        Long parentId = commentRequest.getParentId();
        String text = commentRequest.getText();

        PostComment postComment = new PostComment();
        postComment.setPost(postRepository.findById(postId).orElse(null));
        if (parentId != null) {
            postComment.setParent(postCommentRepository.findById(parentId).orElse(null));
        }
        postComment.setUser(currentUser);
        postComment.setText(text);
        postComment.setTime(Instant.now());

        PostComment savedComment = postCommentRepository.save(postComment);
        return new ResponseEntity<ResponseBasic>(ResponseBasic.builder().id(savedComment.getId()).build(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseBasic> errorByComment() {

        String errorMessage = "Текст комментария не задан или слишком короткий";

        ErrorMessage error = ErrorMessage.builder().text(errorMessage).build();
        ResponseBasic responseBasic = ResponseBasic.builder().result(false).errorMessage(error).build();
        return new ResponseEntity(responseBasic, HttpStatus.OK);
    }
}
