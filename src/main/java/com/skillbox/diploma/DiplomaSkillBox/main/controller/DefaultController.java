package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.UserMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.request.GlobalSettingsRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.AuthResponseTrue;
import com.skillbox.diploma.DiplomaSkillBox.main.response.GlobalSettingsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.InitializeResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.GlobalSettingsService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

import static org.springframework.http.HttpStatus.OK;

@Controller
@Log
public class DefaultController {

    @Autowired
    private GlobalSettingsService globalSettingsService;

    @Autowired
    private AuthService authService;

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

    @PostMapping("/api/settings")
    @ResponseBody
    public GlobalSettingsResponse settings(@RequestBody GlobalSettingsRequest globalSettingsRequest, HttpServletRequest request) {

        User currentUser = authService.getCurrentUser(request);
        if (currentUser != null && currentUser.getIsModerator()) {

            GlobalSettingsResponse globalSettingsResponse = new GlobalSettingsResponse();
            globalSettingsResponse.setMULTIUSER_MODE(globalSettingsRequest.isMULTIUSER_MODE());
            globalSettingsResponse.setPOST_PREMODERATION(globalSettingsRequest.isPOST_PREMODERATION());
            globalSettingsResponse.setSTATISTICS_IS_PUBLIC(globalSettingsRequest.isSTATISTICS_IS_PUBLIC());

        }

        return globalSettingsService.globalSettingsResponse();
    }
}
