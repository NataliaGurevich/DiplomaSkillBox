package com.skillbox.diploma.DiplomaSkillBox.main.mapper;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.response.UserIdNamePhotoResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.UserIdNameResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.UserResponse;
import lombok.Data;

@Data
public class UserMapper {
    private Long id;
    private String name;

  private UserMapper(){}

    public static UserIdNameResponse converterToShortName(User user) {
        UserIdNameResponse userResponse = new UserIdNameResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());

        return userResponse;
    }

    public static UserResponse converterToFullName(User user) {
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());
        userResponse.setEmail(user.getEmail());
        userResponse.setIsModerator(user.getIsModerator());
        userResponse.setIsSettings(user.getIsModerator());
        int count = 56;
        userResponse.setModerationCount(user.getIsModerator() ? count : 0);

        return userResponse;
    }

    public static UserIdNamePhotoResponse converterToNameIdNamePhoto(User user) {
        UserIdNamePhotoResponse userResponse = new UserIdNamePhotoResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());

        return userResponse;
    }
}