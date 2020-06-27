package com.skillbox.diploma.DiplomaSkillBox.main.mapper;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.UserIdNamePhotoResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.UserIdNameResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.UserResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserMapper {

    @Autowired
    private PostRepository postRepository;

    private Long id;
    private String name;

  private UserMapper(){}

    public UserIdNameResponse converterToShortName(User user) {
        UserIdNameResponse userResponse = new UserIdNameResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());

        return userResponse;
    }

    public UserResponse converterToFullName(User user) {
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());
        userResponse.setEmail(user.getEmail());
        userResponse.setIsModerator(user.getIsModerator());
        userResponse.setIsSettings(user.getIsModerator());
        userResponse.setModerationCount(user.getIsModerator() != null && user.getIsModerator() ?
                postRepository.findNewPosts().orElse(0) : 0);

        return userResponse;
    }

    public UserIdNamePhotoResponse converterToNameIdNamePhoto(User user) {
        UserIdNamePhotoResponse userResponse = new UserIdNamePhotoResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());

        return userResponse;
    }
}