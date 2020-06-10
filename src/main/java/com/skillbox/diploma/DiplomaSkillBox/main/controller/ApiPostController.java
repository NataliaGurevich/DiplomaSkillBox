package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PostAddRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostAddService postService;

    @Autowired
    private PostServiceByMode postServiceByMode;

    @Autowired
    private PostServiceByTag postServiceByTag;

    @Autowired
    private PostServiceModeration postServiceModeration;

    @Autowired
    private AuthService authService;

    @Autowired
    private PostServiceBySearch postServiceBySearch;

    @Autowired
    private PostServiceByDate postServiceByDate;

    @GetMapping("")
    public ResponseEntity getAllPost(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                     @RequestParam(value = "limit", defaultValue = "10") int limit,
                                     @RequestParam(value = "mode", defaultValue = "recent") String mode) {

        return new ResponseEntity(postServiceByMode.getSetPosts(offset, limit, mode), HttpStatus.OK);
    }

    @GetMapping("/byDate")
    public ResponseEntity getAllPostByDate(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @RequestParam(value = "limit", defaultValue = "10") int limit,
                                           @RequestParam(value = "date") String date) throws ParseException {

        return new ResponseEntity(postServiceByDate.getPostsByDate(offset, limit, date + "%"), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity getPostById(@PathVariable Long id) {

        return new ResponseEntity(postServiceByMode.getPostById(id), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity addPost(@RequestBody PostAddRequest postAddRequest,
                                  @CookieValue(value = "Token", defaultValue = "") String token) throws ParseException {

        return new ResponseEntity(postService.addNewPost(postAddRequest, token), HttpStatus.OK);
    }

    @GetMapping("/byTag")
    public ResponseEntity getPostByTag(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                       @RequestParam(value = "limit", defaultValue = "10") int limit,
                                       @RequestParam(value = "tag") String tag) {

        return new ResponseEntity(postServiceByTag.getSetPosts(offset, limit, tag), HttpStatus.OK);
    }

    @GetMapping("/moderation")
    public ResponseEntity getPostModeration(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                                            @RequestParam(value = "status", defaultValue = "new") String status,
                                            @CookieValue(value = "Token", defaultValue = "") String token) {
        User currentUser = authService.getCurrentUser(token);
        if (currentUser.getIsModerator()) {
            log.info("MODERATION {}", currentUser);
            return new ResponseEntity(postServiceModeration.getSetPosts(offset, limit, status.toUpperCase(), currentUser),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.OK);
        }
    }

    @GetMapping("/search")
    public ResponseEntity getPostBySearch(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                          @RequestParam(value = "limit", defaultValue = "10") int limit,
                                          @RequestParam(value = "query", defaultValue = "", required = false) String querySearch) throws InterruptedException {

        return new ResponseEntity(postServiceBySearch.getPostsBySearch(offset, limit, querySearch.trim()), HttpStatus.OK);
    }
}

