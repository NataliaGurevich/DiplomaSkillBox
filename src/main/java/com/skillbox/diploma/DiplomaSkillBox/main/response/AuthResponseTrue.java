package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class AuthResponseTrue {
    private boolean result = true;
    private UserResponse user;
}
