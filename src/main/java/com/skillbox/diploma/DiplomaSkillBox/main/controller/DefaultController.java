package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.request.GlobalSettingsRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.GlobalSettingsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.InitializeResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.GlobalSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
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

    @RequestMapping(value = "/api/settings", method = RequestMethod.GET)
    public ResponseEntity settingsGet() {
        return new ResponseEntity(globalSettingsService.globalSettingsResponse(), OK);
    }

    @RequestMapping(value = "/api/settings", method = RequestMethod.PUT)
    public ResponseEntity settingsPut(@RequestBody GlobalSettingsRequest globalSettingsRequest,
                                      @CookieValue(value = "Token", defaultValue = "") String token) {

        User currentUser = authService.getCurrentUser(token);

        log.info("IN GLOBALSETTINGS POST user {}", currentUser);

        if (currentUser != null && currentUser.getIsModerator()) {

            GlobalSettingsResponse globalSettingsResponse = new GlobalSettingsResponse();
            globalSettingsResponse.setMULTIUSER_MODE(globalSettingsRequest.isMULTIUSER_MODE());
            globalSettingsResponse.setPOST_PREMODERATION(globalSettingsRequest.isPOST_PREMODERATION());
            globalSettingsResponse.setSTATISTICS_IS_PUBLIC(globalSettingsRequest.isSTATISTICS_IS_PUBLIC());

            globalSettingsService.saveGlobalSettings(globalSettingsRequest);

            return new ResponseEntity(globalSettingsResponse, OK);
        }

        return new ResponseEntity(globalSettingsService.globalSettingsResponse(), OK);
    }
}
