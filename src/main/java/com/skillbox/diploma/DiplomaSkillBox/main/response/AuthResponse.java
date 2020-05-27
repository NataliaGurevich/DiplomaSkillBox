package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class AuthResponse {
    private boolean result;
    private UserResponse user;
}
