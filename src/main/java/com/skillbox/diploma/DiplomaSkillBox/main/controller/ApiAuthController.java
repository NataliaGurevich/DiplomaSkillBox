package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.response.AuthResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/check")
    public AuthResponse getCheck() {
        return authService.check();
    }
}
