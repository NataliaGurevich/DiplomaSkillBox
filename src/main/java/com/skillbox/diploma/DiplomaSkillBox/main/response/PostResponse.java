package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.util.Date;

@Data
public class PostResponse implements Comparable<PostResponse> {
    private Long id;
    private Date time;
    private UserIdNameResponse user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;


    @Override
    public int compareTo(PostResponse o) {
        return this.id.compareTo(o.getId());
    }
}
