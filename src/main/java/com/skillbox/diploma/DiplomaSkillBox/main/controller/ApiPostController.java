package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @GetMapping("/api/post/")
    public String getPost() {
        return null;
    }
}
