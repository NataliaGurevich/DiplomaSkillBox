package com.skillbox.diploma.DiplomaSkillBox.main.mapper;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostCommentRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostVoteRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostUtil {

    private final PostVoteRepository postVoteRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostUtil(PostVoteRepository postVoteRepository, PostCommentRepository postCommentRepository, PostMapper postMapper) {
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;
        this.postMapper = postMapper;
    }

    public List<PostResponse> cretePostList(List<Post> postList) {
        List<PostResponse> posts = new ArrayList<>();

        for (Post post : postList) {
            int likeCount = postVoteRepository.findCountLikes(post.getId()).orElse(0);
            int disLikeCount = postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            int commentCount = postCommentRepository.findCountComments(post.getId()).orElse(0);
            posts.add(postMapper.converter(post, likeCount, disLikeCount, commentCount));
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
