package com.skillbox.diploma.DiplomaSkillBox.main.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private String text;

    private UserIdNamePhotoResponse user;
}
