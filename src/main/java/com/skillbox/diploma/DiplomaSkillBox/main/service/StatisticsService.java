package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostVoteRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PostAddRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.StatisticResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class StatisticsService {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;

    @Autowired
    public StatisticsService(PostRepository postRepository, PostVoteRepository postVoteRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
    }

    public StatisticResponse myStatistics(User currentUser){

        StatisticResponse statisticResponse = new StatisticResponse();

        List<Post> posts = postRepository.findPostsByUser(currentUser.getId()).orElse(null);

        int postsCount = 0;
        int viewsCount = 0;
        int likesCount = 0;
        int dislikesCount = 0;

        if (posts != null){
            postsCount = posts.size();

            for (Post post : posts) {
                viewsCount += post.getViewCount();
                likesCount += postVoteRepository.findCountLikes(post.getId()).orElse(0);
                dislikesCount += postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            }
        }

        Date datePublication = postRepository.findFirstPublicationByUser(currentUser.getId()).orElse(null);
        String firstPublication = datePublication != null ? datePublication.toString().substring(0, 16) : "";
        statisticResponse.setPostsCount(postsCount);
        statisticResponse.setLikesCount(likesCount);
        statisticResponse.setDislikesCount(dislikesCount);
        statisticResponse.setViewsCount(viewsCount);
        statisticResponse.setFirstPublication(firstPublication);

        return statisticResponse;
    }

    public StatisticResponse allStatistics(){

        StatisticResponse statisticResponse = new StatisticResponse();

        List<Post> posts = postRepository.findAllPosts().orElse(null);

        int postsCount = 0;
        int viewsCount = 0;
        int likesCount = 0;
        int dislikesCount = 0;

        if (posts != null){
            postsCount = posts.size();

            for (Post post : posts) {
                viewsCount += post.getViewCount();
                likesCount += postVoteRepository.findCountLikes(post.getId()).orElse(0);
                dislikesCount += postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            }
        }

        Date datePublication = postRepository.findFirstPublication().orElse(null);
        String firstPublication = datePublication != null ? datePublication.toString().substring(0, 16) : "";
        statisticResponse.setPostsCount(postsCount);
        statisticResponse.setLikesCount(likesCount);
        statisticResponse.setDislikesCount(dislikesCount);
        statisticResponse.setViewsCount(viewsCount);
        statisticResponse.setFirstPublication(firstPublication);

        return statisticResponse;
    }
}
