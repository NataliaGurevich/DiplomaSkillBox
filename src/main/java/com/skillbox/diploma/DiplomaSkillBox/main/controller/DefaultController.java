package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.response.GlobalSettingsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.InitializeResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.GlobalSettingsService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Log
public class DefaultController {

    @Autowired
    private GlobalSettingsService globalSettingsService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/api/init")
    @ResponseBody
    public InitializeResponse init() {
        return new InitializeResponse();
    }

    @GetMapping("/api/settings")
    @ResponseBody
    public GlobalSettingsResponse settings() {
        return globalSettingsService.globalSettingsResponse();
    }
}
