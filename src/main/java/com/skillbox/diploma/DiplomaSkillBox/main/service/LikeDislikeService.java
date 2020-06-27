package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.PostVote;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostVoteRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.LikeDislikeRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
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

    public ResponseBasic setLikeDislike(LikeDislikeRequest likeDislikeRequest, Boolean value, User currentUser) {

        Long postId = likeDislikeRequest.getPostId();
        Post post = postRepository.findById(postId).orElse(null);
        User postOwner = post.getUser();

        PostVote postVoteWithLike;
        boolean valueBefore;

        Optional<PostVote> postVote = postVoteRepository.findByPostAndUser(postId, currentUser.getId());

        if (postOwner != currentUser) {
            PostVote postLikeDislike;
            if (postVote.isEmpty()) {
                ResponseBasic responseBasic = ResponseBasic
                        .builder()
                        .result(setNewValue(post, currentUser, value))
                        .build();
                return responseBasic;
            } else {
                postLikeDislike = postVote.get();
                valueBefore = postLikeDislike.getValue();
                if (valueBefore == value) {
                    ResponseBasic responseBasic = ResponseBasic
                            .builder()
                            .result(false)
                            .build();
                    return responseBasic;
                } else {
                    postLikeDislike.setTime(Instant.now());
                    postLikeDislike.setValue(value);
                    postVoteWithLike = postVoteRepository.save(postLikeDislike);

                    if (postVoteWithLike != null) {
                        ResponseBasic responseBasic = ResponseBasic
                                .builder()
                                .result(true)
                                .build();
                        return responseBasic;
                    } else {
                        ResponseBasic responseBasic = ResponseBasic
                                .builder()
                                .result(false)
                                .build();
                        return responseBasic;
                    }
                }
            }
        } else {
            ResponseBasic responseBasic = ResponseBasic
                    .builder()
                    .result(false)
                    .build();
            return responseBasic;
        }
    }

    public boolean setNewValue(Post post, User currentUser, boolean value) {
        PostVote postLikeDislike = new PostVote();
        postLikeDislike.setPost(post);
        postLikeDislike.setUser(currentUser);
        postLikeDislike.setTime(Instant.now());
        postLikeDislike.setValue(value);
        PostVote postVoteWithLike = postVoteRepository.save(postLikeDislike);

        return postVoteWithLike == null ? false : true;
    }
}
