package com.skillbox.diploma.DiplomaSkillBox.main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Registration {
    private String name;
    private String password;
    private String captcha;

    @JsonProperty("e_mail")
    private String email;

    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
