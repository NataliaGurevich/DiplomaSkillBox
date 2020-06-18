package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class ErrorMessage {

    private String email;
    private String name;
    private String password;
    private String captcha;
    private String code;
    private String photo;
}
