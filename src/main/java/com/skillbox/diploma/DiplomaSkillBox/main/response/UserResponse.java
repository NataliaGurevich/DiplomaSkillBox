package com.skillbox.diploma.DiplomaSkillBox.main.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserResponse {

    private Long id;

    @JsonProperty("moderation")
    private Boolean isModerator;

    private String name;

    private String email;

    private String photo;

    @JsonProperty("settings")
    private Boolean isSettings;

    private int moderationCount;

}
