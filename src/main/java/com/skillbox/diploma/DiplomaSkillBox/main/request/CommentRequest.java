package com.skillbox.diploma.DiplomaSkillBox.main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentRequest {

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("text")
    private String text;
}
