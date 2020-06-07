package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.util.List;

@Data
public class PostByTagResponse {
    private Long id;
    private String time;
    private UserIdNameResponse user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private List<CommentResponse> comments;
}
