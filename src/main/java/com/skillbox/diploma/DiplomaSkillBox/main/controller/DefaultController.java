package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.request.GlobalSettingsRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.ProfileRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CalendarResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.GlobalSettingsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.InitializeResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
import com.skillbox.diploma.DiplomaSkillBox.main.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Controller
public class DefaultController {

    private final GlobalSettingsService globalSettingsService;
    private final AuthService authService;
    private final CalendarService calendarService;
    private final PostServiceByMode postServiceByMode;
    private final InitializeResponse init;
    private final FileUploadService fileUploadService;
    private final ProfileService profileService;

    @Autowired
    public DefaultController(GlobalSettingsService globalSettingsService, AuthService authService, CalendarService calendarService, PostServiceByMode postServiceByMode, InitializeResponse init, FileUploadService fileUploadService, ProfileService profileService) {
        this.globalSettingsService = globalSettingsService;
        this.authService = authService;
        this.calendarService = calendarService;
        this.postServiceByMode = postServiceByMode;
        this.init = init;
        this.fileUploadService = fileUploadService;
        this.profileService = profileService;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/api/init")
    @ResponseBody
    public InitializeResponse init() {
        return init;
    }

    @RequestMapping(value = "/api/settings", method = RequestMethod.GET)
    public ResponseEntity<GlobalSettingsResponse> settingsGet() {

        return new ResponseEntity<>(globalSettingsService.setGlobalSettings(), OK);
    }

    @RequestMapping(value = "/api/settings", method = RequestMethod.PUT)
    public ResponseEntity<GlobalSettingsResponse> settingsPut(@RequestBody GlobalSettingsRequest globalSettingsRequest,
                                                              @CookieValue(value = "Token", defaultValue = "") String token) {

        User currentUser = authService.getCurrentUser(token);

        log.info("IN GLOBALSETTINGS POST user {}", currentUser);

        if (currentUser != null && currentUser.getIsModerator()) {

            return new ResponseEntity<>(globalSettingsService.setGlobalSettings(globalSettingsRequest), OK);
        }

        return new ResponseEntity<>(globalSettingsService.setGlobalSettings(), OK);
    }

    @RequestMapping(value = "/api/calendar{year}", method = RequestMethod.GET)
    public ResponseEntity<CalendarResponse> calendar(@PathVariable String year) {

        return new ResponseEntity<>(calendarService.postsPerDate(year), OK);
    }

    @RequestMapping(value = "/login/change-password/{code}", method = RequestMethod.GET)
    public String restorePassword(@PathVariable String code) {

        return "forward:/";
    }

    //    @PostMapping(value = "/api/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping(value = "/api/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image,
                                         @CookieValue(value = "Token", defaultValue = "") String token) throws IOException {

        User currentUser = authService.getCurrentUser(token);

        if (currentUser != null) {

            return fileUploadService.uploadImage(image);
        }
        return new ResponseEntity<>(null, UNAUTHORIZED);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseBasic> profile(@RequestParam("photo") MultipartFile photo,
                                                 @RequestParam String name,
                                                 @RequestParam String email,
                                                 @RequestParam(required = false, defaultValue = "") String password,
                                                 @RequestParam(required = false, defaultValue = "0") int removePhoto,
                                                 @CookieValue(value = "Token", defaultValue = "") String token) throws IOException {

        User currentUser = authService.getCurrentUser(token);

        if (currentUser != null) {
            return new ResponseEntity<>(profileService.changeProfile(photo, name, email, password, removePhoto, currentUser), OK);
        }
        return new ResponseEntity<>(null, UNAUTHORIZED);
    }

    @PostMapping(value = "/api/profile/my")
    public ResponseEntity<ResponseBasic> profile(@RequestBody ProfileRequest profileRequest,
                                                 @CookieValue(value = "Token", defaultValue = "") String token) {

        User currentUser = authService.getCurrentUser(token);

        if (currentUser != null) {
            return new ResponseEntity<>(profileService.changeProfile(profileRequest, currentUser), OK);
        }
        return new ResponseEntity<>(null, UNAUTHORIZED);
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{path:[^\\.]*}")
    public String redirectToIndex() {
        return "forward:/";
    }
}
