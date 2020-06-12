package com.skillbox.diploma.DiplomaSkillBox.main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ModerationRequest {

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("decision")
    private String decision;

}
