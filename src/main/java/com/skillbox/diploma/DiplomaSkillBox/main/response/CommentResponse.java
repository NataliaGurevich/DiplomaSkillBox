package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.util.Date;

@Data
public class CommentResponse {

    private Long id;

    private Date time;

    private String text;

    private UserIdNamePhotoResponse user;
}
