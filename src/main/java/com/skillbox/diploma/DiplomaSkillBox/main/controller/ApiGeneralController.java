package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.request.ModerationRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.PostServiceModeration;
import com.skillbox.diploma.DiplomaSkillBox.main.service.StatisticsService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final TagService tagService;
    private final AuthService authService;
    private final PostServiceModeration postServiceModeration;
    private final StatisticsService statisticsService;

    @Autowired
    public ApiGeneralController(TagService tagService, AuthService authService,
                                PostServiceModeration postServiceModeration,
                                StatisticsService statisticsService) {
        this.tagService = tagService;
        this.authService = authService;
        this.postServiceModeration = postServiceModeration;
        this.statisticsService = statisticsService;
    }

    @GetMapping("")
    @RequestMapping("/tag")
    public ResponseEntity getAllTag(@RequestParam(name = "query", required = false) String query) {

        query = (query == null || query.trim().length() == 0) ? "" : query.trim();

        return new ResponseEntity(tagService.getAllTags(query), HttpStatus.OK);

    }


    @PostMapping("/moderation")
    public ResponseEntity setPostModeration(@RequestBody ModerationRequest moderationRequest,
                                            @CookieValue(value = "Token", defaultValue = "") String token) {
        User currentUser = authService.getCurrentUser(token);
        if (currentUser.getIsModerator()) {
            log.info("MODERATION {}", currentUser);
            return new ResponseEntity(postServiceModeration.setModeration(moderationRequest, currentUser),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/statistics/my")
    public ResponseEntity statisticsMy(@CookieValue(value = "Token", defaultValue = "") String token) {
        User currentUser = authService.getCurrentUser(token);
        if (currentUser != null) {
            log.info("STATISTIC MY {}", currentUser);
            return new ResponseEntity(statisticsService.myStatistics(currentUser),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/statistics/all")
    public ResponseEntity statisticsAll() {
        return new ResponseEntity(statisticsService.allStatistics(), HttpStatus.OK);
    }
}
