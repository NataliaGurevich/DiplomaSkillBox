package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.GlobalSettingsRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.CommentRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.ModerationRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
import com.skillbox.diploma.DiplomaSkillBox.main.response.StatisticResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TagsResponse;
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
    public ResponseEntity<TagsResponse> getAllTag(@RequestParam(name = "query", required = false) String query) {

        return tagService.getAllTags(query);
    }


    @PostMapping("/moderation")
    public ResponseEntity<ResponseBasic> setPostModeration(@RequestBody ModerationRequest moderationRequest,
                                                           @CookieValue(value = "Token", defaultValue = "") String token) {
        User currentUser = authService.getCurrentUser(token);
        if (currentUser.getIsModerator()) {
            return postServiceModeration.setModeration(moderationRequest, currentUser);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/statistics/my")
    public ResponseEntity<StatisticResponse> statisticsMy(@CookieValue(value = "Token", defaultValue = "") String token) {
        User currentUser = authService.getCurrentUser(token);
        if (currentUser != null) {
            return statisticsService.myStatistics(currentUser);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticResponse> statisticsAll(@CookieValue(value = "Token", defaultValue = "") String token) {

        User currentUser = authService.getCurrentUser(token);
        if (globalSettingsRepository.findSettingsValueByCode(statistics) || currentUser != null) {
            return statisticsService.allStatistics();
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<ResponseBasic> commentToPost(@RequestBody CommentRequest commentRequest,
                                                       @CookieValue(value = "Token", defaultValue = "") String token) {

        User currentUser = authService.getCurrentUser(token);
        if (currentUser != null) {
            return new ResponseEntity<>(commentService.addCommentOrErrorByComment(commentRequest, currentUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
