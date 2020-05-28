package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.response.PostCommentsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.PostService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostService postService;

    @GetMapping("")
    public PostsResponse getAllPost(@RequestParam(value="offset", defaultValue = "0", required = false) int offset,
                                    @RequestParam(value="limit", defaultValue = "10", required = false) int limit,
                                    @RequestParam(value="mode", defaultValue = "popular", required = false) String mode) {
        return postService.getAllPost();
    }

    @GetMapping("{id}")
    public PostCommentsResponse getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }
}
