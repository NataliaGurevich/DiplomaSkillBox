package com.skillbox.diploma.DiplomaSkillBox.main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Login {

    @JsonProperty("e_mail")
    private String email;

    private String password;
}
