package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class CaptchaResponse {
    private String secret;
    private String image;
}
