package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostCommentsResponse implements Comparable<PostCommentsResponse> {
    private Long id;
    private Date time;
    private UserResponse user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private List<CommentResponse> comments;
    private List<String> tags;

    @Override
    public int compareTo(PostCommentsResponse o) {
        return this.id.compareTo(o.getId());
    }
}
