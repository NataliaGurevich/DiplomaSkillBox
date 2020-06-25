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
import java.util.Optional;

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
        boolean valueBefore;

        Optional<PostVote> postVote = postVoteRepository.findByPostAndUser(postId, currentUser.getId());

        if (postOwner != currentUser) {
            PostVote postLikeDislike;
            if (postVote.isEmpty()) {
                return new TrueFalseResponse(setNewValue(post, currentUser, value));
            } else {
                postLikeDislike = postVote.get();
                valueBefore = postLikeDislike.getValue();
                if (valueBefore == value) {
                    return new TrueFalseResponse(false);
                }
                else {
                    postLikeDislike.setTime(Instant.now());
                    postLikeDislike.setValue(value);
                    postVoteWithLike = postVoteRepository.save(postLikeDislike);

                    if (postVoteWithLike != null) {
                        return new TrueFalseResponse(true);
                    } else {
                        return new TrueFalseResponse(false);
                    }
                }
            }
        } else {
            return new TrueFalseResponse(false);
        }
    }

    public boolean setNewValue(Post post, User currentUser, boolean value){
        PostVote postLikeDislike = new PostVote();
        postLikeDislike.setPost(post);
        postLikeDislike.setUser(currentUser);
        postLikeDislike.setTime(Instant.now());
        postLikeDislike.setValue(value);
        PostVote postVoteWithLike = postVoteRepository.save(postLikeDislike);

        return postVoteWithLike == null ? false : true;
    }
}
