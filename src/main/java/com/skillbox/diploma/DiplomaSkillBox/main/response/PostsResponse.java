package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.util.List;

@Data
public class PostsResponse <T>{
    private long count;
    private List<T> posts;
}
