package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.request.GlobalSettingsRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.ProfileRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.GlobalSettingsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.InitializeResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Calendar;

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

    private Calendar calendar = Calendar.getInstance();
    private String currentYear = Integer.toString(calendar.get(Calendar.YEAR));

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

    @RequestMapping(value = "/api/calendar{year}", method = RequestMethod.GET)
    public ResponseEntity calendar(@PathVariable String year) {
        try {
            year = (year == null || Integer.parseInt(year) > Integer.parseInt(currentYear) || year.length() < 4) ?
                    currentYear : year;
        } catch (NumberFormatException ex) {
            year = currentYear;
        }

        return new ResponseEntity(calendarService.postsPerDate(year), OK);
    }

    @RequestMapping(value = "/posts/recent", method = RequestMethod.GET)
    public ResponseEntity defaultPage(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                      @RequestParam(value = "limit", defaultValue = "10") int limit,
                                      @RequestParam(value = "mode", defaultValue = "recent") String mode) {

        return new ResponseEntity(postServiceByMode.getSetPosts(offset, limit, mode), HttpStatus.OK);
    }

    @RequestMapping(value = "/login/change-password/{code}", method = RequestMethod.GET)
    public String restorePassword(@PathVariable String code) {

        return "forward:/";
    }

    //    @PostMapping(value = "/api/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping(value = "/api/image")
    public ResponseEntity uploadImage(@RequestParam("image") MultipartFile image,
                                      @CookieValue(value = "Token", defaultValue = "") String token) throws IOException {

        User currentUser = authService.getCurrentUser(token);

        if (currentUser != null) {
            if (image != null && !image.isEmpty()) {
                return new ResponseEntity(fileUploadService.fileUpload(image), OK);
            }
        }
        return new ResponseEntity(null, UNAUTHORIZED);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity profile(@RequestParam("photo") MultipartFile photo,
                                  @RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam(required = false, defaultValue = "") String password,
                                  @RequestParam(required = false, defaultValue = "0") int removePhoto,
                                  @CookieValue(value = "Token", defaultValue = "") String token) throws IOException, ServletException {

        User currentUser = authService.getCurrentUser(token);

        log.info("PROFILE  file {}", photo.getOriginalFilename());
        log.info("PROFILE  name {}", name);
        log.info("PROFILE  email {}", email);
        log.info("PROFILE  password {}", password);
        log.info("PROFILE  removePhoto {}", removePhoto);
        if (currentUser != null) {
            return (profileService.changeProfile(photo, name, email, password, removePhoto, currentUser));
        }
        return new ResponseEntity(null, UNAUTHORIZED);
    }

    @PostMapping(value = "/api/profile/my")
    public ResponseEntity profile(@RequestBody ProfileRequest profileRequest,
                                  @CookieValue(value = "Token", defaultValue = "") String token) throws IOException, ServletException {

        User currentUser = authService.getCurrentUser(token);

        log.info("PROFILE  name {}", profileRequest.getName());
        log.info("PROFILE  email {}", profileRequest.getEmail());
        log.info("PROFILE  password {}", profileRequest.getPassword());
        log.info("PROFILE  removePhoto {}", profileRequest.getRemovePhoto());

        if (currentUser != null) {
            return (profileService.changeProfile(profileRequest, currentUser));
        }
        return new ResponseEntity(null, UNAUTHORIZED);
    }
}
