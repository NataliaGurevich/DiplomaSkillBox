package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.response.Initialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @Autowired
    private Initialize init;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/api/init/")
    public ResponseEntity<Initialize> init() {
        System.out.println(init);
        return new ResponseEntity<>(init, HttpStatus.OK);
    }
}
