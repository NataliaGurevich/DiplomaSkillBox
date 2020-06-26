package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.LikeDislikeRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PostAddRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostCommentsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
import com.skillbox.diploma.DiplomaSkillBox.main.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostAddService postService;
    private final PostServiceByMode postServiceByMode;
    private final PostServiceByTag postServiceByTag;
    private final PostServiceModeration postServiceModeration;
    private final AuthService authService;
    private final PostServiceBySearch postServiceBySearch;
    private final PostServiceByDate postServiceByDate;
    private final PostServiceMyPost postServiceMyPost;
    private final PostServiceById postServiceById;
    private final PostRepository postRepository;
    private final LikeDislikeService likeDislikeService;

    @Autowired
    public ApiPostController(PostAddService postService, PostServiceByMode postServiceByMode, PostServiceByTag postServiceByTag, PostServiceModeration postServiceModeration, AuthService authService, PostServiceBySearch postServiceBySearch, PostServiceByDate postServiceByDate, PostServiceMyPost postServiceMyPost, PostServiceById postServiceById, PostRepository postRepository, LikeDislikeService likeDislikeService) {
        this.postService = postService;
        this.postServiceByMode = postServiceByMode;
        this.postServiceByTag = postServiceByTag;
        this.postServiceModeration = postServiceModeration;
        this.authService = authService;
        this.postServiceBySearch = postServiceBySearch;
        this.postServiceByDate = postServiceByDate;
        this.postServiceMyPost = postServiceMyPost;
        this.postServiceById = postServiceById;
        this.postRepository = postRepository;
        this.likeDislikeService = likeDislikeService;
    }

    @GetMapping("")
    public ResponseEntity<PostsResponse> getAllPost(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                    @RequestParam(value = "mode", defaultValue = "recent") String mode) {

        return postServiceByMode.getSetPosts(offset, limit, mode);
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostsResponse> getAllPostByDate(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                          @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                          @RequestParam(value = "date") String date) throws ParseException {

        return postServiceByDate.getPostsByDate(offset, limit, date + "%");
    }

    @GetMapping("{id}")
    public ResponseEntity<PostCommentsResponse> getPostById(@PathVariable Long id,
                                                            @CookieValue(value = "Token", defaultValue = "") String token) throws ParseException {
        User currentUser = authService.getCurrentUser(token);
        return postServiceByMode.getPostById(id, currentUser);
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseBasic> editPostById(@PathVariable Long id,
                                                      @RequestBody PostAddRequest postAddRequest,
                                                      @CookieValue(value = "Token", defaultValue = "") String token) throws ParseException {
        User currentUser = authService.getCurrentUser(token);
        Post post = postRepository.findById(id).orElse(null);

        if (currentUser != null && post != null && (post.getUser().equals(currentUser) || currentUser.getIsModerator())) {
            log.info("IN EDIT POST currentUser {}", currentUser.getName());
            return postServiceById.editPostById(id, postAddRequest, currentUser);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("")
    public ResponseEntity<ResponseBasic> addPost(@RequestBody PostAddRequest postAddRequest,
                                                 @CookieValue(value = "Token", defaultValue = "") String token) throws ParseException {

        return postService.addNewPost(postAddRequest, token);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostsResponse> getPostByTag(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                      @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                      @RequestParam(value = "tag") String tag) {

        return postServiceByTag.getSetPosts(offset, limit, tag);
    }

    @GetMapping("/moderation")
    public ResponseEntity<PostsResponse> getPostModeration(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                           @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                           @RequestParam(value = "status", defaultValue = "new") String status,
                                                           @CookieValue(value = "Token", defaultValue = "") String token) {
        User currentUser = authService.getCurrentUser(token);
        if (currentUser.getIsModerator()) {
            log.info("MODERATION {}", currentUser);
            return postServiceModeration.getSetPosts(offset, limit, status.toUpperCase(), currentUser);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<PostsResponse> getPostBySearch(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                         @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                         @RequestParam(value = "query", defaultValue = "", required = false) String querySearch) throws InterruptedException {

        return postServiceBySearch.getPostsBySearch(offset, limit, querySearch.trim());
    }

    @GetMapping("/my")
    public ResponseEntity<PostsResponse> getMyPost(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                   @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                   @RequestParam(value = "status", defaultValue = "inactive") String status,
                                                   @CookieValue(value = "Token", defaultValue = "") String token) {
        User currentUser = authService.getCurrentUser(token);

        if (currentUser != null) {
            log.info("IN MY currentUser {}", currentUser.getName());
            return postServiceMyPost.getMyPosts(offset, limit, status, currentUser);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/like")
    public ResponseEntity<ResponseBasic> like(@RequestBody LikeDislikeRequest likeDislikeRequest,
                                              @CookieValue(value = "Token", defaultValue = "") String token) {
        User currentUser = authService.getCurrentUser(token);

        if (currentUser != null) {
            return likeDislikeService.setLikeDislike(likeDislikeRequest, true, currentUser);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/dislike")
    public ResponseEntity<ResponseBasic> dislike(@RequestBody LikeDislikeRequest likeDislikeRequest,
                                                 @CookieValue(value = "Token", defaultValue = "") String token) {
        User currentUser = authService.getCurrentUser(token);

        if (currentUser != null) {
            return likeDislikeService.setLikeDislike(likeDislikeRequest, false, currentUser);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

}

