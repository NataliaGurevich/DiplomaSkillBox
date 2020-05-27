package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.response.TagsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.TagService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("/api/tag")
public class ApiGeneralController {

    @Autowired
    private TagService tagService;

    @GetMapping("")
    public TagsResponse getAllTag() {
        log.info(tagService.getAllTags().toString());
        return tagService.getAllTags();
    }
}
