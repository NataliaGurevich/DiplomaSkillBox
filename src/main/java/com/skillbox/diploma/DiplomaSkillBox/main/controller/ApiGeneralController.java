package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.request.ModerationRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TagsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.PostServiceModeration;
import com.skillbox.diploma.DiplomaSkillBox.main.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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

    @Autowired
    public ApiGeneralController(TagService tagService, AuthService authService, PostServiceModeration postServiceModeration) {
        this.tagService = tagService;
        this.authService = authService;
        this.postServiceModeration = postServiceModeration;
    }

    @GetMapping("")
    @RequestMapping("/tag")
    public ResponseEntity getAllTag() {
        return new ResponseEntity(tagService.getAllTags(), HttpStatus.OK);
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
}
