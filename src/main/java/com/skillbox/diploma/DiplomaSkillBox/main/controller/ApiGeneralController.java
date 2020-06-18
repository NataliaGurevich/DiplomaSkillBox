package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.GlobalSettingsRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.CommentRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.ModerationRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorText;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorTextResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final CommentService commentService;
    private final GlobalSettingsRepository globalSettingsRepository;

    @Value("${global.settings.statistics}")
    private String statistics;

    @Autowired
    public ApiGeneralController(TagService tagService, AuthService authService,
                                PostServiceModeration postServiceModeration,
                                StatisticsService statisticsService, CommentService commentService, GlobalSettingsRepository globalSettingsRepository) {
        this.tagService = tagService;
        this.authService = authService;
        this.postServiceModeration = postServiceModeration;
        this.statisticsService = statisticsService;
        this.commentService = commentService;
        this.globalSettingsRepository = globalSettingsRepository;
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
        } else if (currentUser == null && globalSettingsRepository.findSettingsValueByCode(statistics)) {
            return new ResponseEntity(statisticsService.allStatistics(), HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/statistics/all")
    public ResponseEntity statisticsAll(@CookieValue(value = "Token", defaultValue = "") String token) {

        User currentUser = authService.getCurrentUser(token);
        if (globalSettingsRepository.findSettingsValueByCode(statistics) || currentUser != null) {
            return new ResponseEntity(statisticsService.allStatistics(), HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/comment")
    public ResponseEntity commentToPost(@RequestBody CommentRequest commentRequest,
                                        @CookieValue(value = "Token", defaultValue = "") String token) {
        String errorMessage = "Текст комментария не задан или слишком короткий";
        User currentUser = authService.getCurrentUser(token);
        if (currentUser != null) {
            if (commentRequest.getText() == null || commentRequest.getText().length() < 3) {
                return new ResponseEntity(new ErrorTextResponse(
                        new ErrorText(errorMessage)), HttpStatus.OK);
            } else {
                return new ResponseEntity(commentService.addComment(commentRequest, currentUser), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
