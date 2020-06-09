package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.response.TagsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    @Autowired
    private TagService tagService;

    @GetMapping("")
    @RequestMapping("/tag")
    public ResponseEntity getAllTag() {
        return new ResponseEntity(tagService.getAllTags(), HttpStatus.OK);
    }
}
