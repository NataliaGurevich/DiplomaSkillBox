package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.request.GlobalSettingsRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.GlobalSettingsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.InitializeResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.CalendarService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.GlobalSettingsService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.PostServiceByMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Year;
import java.util.Calendar;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
public class DefaultController {

    private final GlobalSettingsService globalSettingsService;
    private final AuthService authService;
    private final CalendarService calendarService;
    private final PostServiceByMode postServiceByMode;
    private final InitializeResponse init;

    private Calendar calendar = Calendar.getInstance();
    private String currentYear = Integer.toString(calendar.get(Calendar.YEAR));

    @Autowired
    public DefaultController(GlobalSettingsService globalSettingsService, AuthService authService, CalendarService calendarService, PostServiceByMode postServiceByMode, InitializeResponse init) {
        this.globalSettingsService = globalSettingsService;
        this.authService = authService;
        this.calendarService = calendarService;
        this.postServiceByMode = postServiceByMode;
        this.init = init;
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
    public ResponseEntity calendar(@PathVariable String year){
        try{
            year = (year == null || Integer.parseInt(year) > Integer.parseInt(currentYear) || year.length() < 4) ?
                    currentYear : year;
        }
        catch (NumberFormatException ex) {
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
}
