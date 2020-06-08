package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentResponse {

    private Long id;

    private Instant time;

    private String text;

    private UserIdNamePhotoResponse user;
}
