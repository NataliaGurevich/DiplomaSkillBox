package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.PostVote;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostVoteRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.LikeDislikeRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TrueFalseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class LikeDislikeService {

    private final PostVoteRepository postVoteRepository;
    private final PostRepository postRepository;

    @Autowired
    public LikeDislikeService(PostVoteRepository postVoteRepository, PostRepository postRepository) {
        this.postVoteRepository = postVoteRepository;
        this.postRepository = postRepository;
    }

    public TrueFalseResponse setLikeDislike(LikeDislikeRequest likeDislikeRequest, Boolean value, User currentUser) {

        Long postId = likeDislikeRequest.getPostId();
        Post post = postRepository.findById(postId).orElse(null);
        User postOwner = post.getUser();

        PostVote postVoteWithLike;

        PostVote postVote = postVoteRepository.findByPostAndUser(postId, postOwner.getId()).orElse(null);

        if (postOwner != currentUser) {
            if (postVote == null) {
                postVote = new PostVote();
                postVote.setPost(post);
                postVote.setUser(currentUser);
                postVote.setTime(Instant.now());
                postVote.setValue(value);
            } else {
                postVote.setTime(Instant.now());
                postVote.setValue(value);
            }
            postVoteWithLike = postVoteRepository.save(postVote);
            if (postVoteWithLike == null) {
                return new TrueFalseResponse(false);
            }
            return new TrueFalseResponse(true);
        } else {
            return new TrueFalseResponse(false);
        }


    }
}
