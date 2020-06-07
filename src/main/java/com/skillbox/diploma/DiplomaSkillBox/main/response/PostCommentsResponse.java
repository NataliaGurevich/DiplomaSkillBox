package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.util.List;

@Data
public class PostCommentsResponse {
    private Long id;
    private String time;
    private UserIdNameResponse user;
    private String title;
    private String announce;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private List<CommentResponse> comments;
    private List<String> tags;
}
