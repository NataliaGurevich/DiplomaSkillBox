package com.skillbox.diploma.DiplomaSkillBox.main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {

    private String email;
    private String name;
    private String password;
    private String captcha;
    private String code;
    private String photo;
    private String image;
    private String text;
    private String title;
    private String message;
}
