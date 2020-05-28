package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentsResponse {

    private List<CommentResponse> comments;
}
