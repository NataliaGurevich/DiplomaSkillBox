package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class PostResponse {
    private Long id;
    private String time;
    private UserIdNameResponse user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
}
