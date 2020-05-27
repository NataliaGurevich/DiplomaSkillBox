package com.skillbox.diploma.DiplomaSkillBox.main.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserIdNamePhotoResponse {

    private Long id;

    private String name;

    private String photo;
}
