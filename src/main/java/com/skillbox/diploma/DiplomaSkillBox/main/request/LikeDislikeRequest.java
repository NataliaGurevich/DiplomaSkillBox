package com.skillbox.diploma.DiplomaSkillBox.main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LikeDislikeRequest {

    @JsonProperty("post_id")
    private Long postId;
}
