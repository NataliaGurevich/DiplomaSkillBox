package com.skillbox.diploma.DiplomaSkillBox.main.mapper;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostResponse;
import lombok.Data;

@Data
public class PostMapper {

    private PostMapper(){}

    public static PostResponse converter(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.setTime(post.getTime());
        postResponse.setUser(UserMapper.converterToShortName(post.getUser()));
        postResponse.setTitle(post.getTitle());
        postResponse.setAnnounce(post.getTitle());

        return postResponse;
    }
}
