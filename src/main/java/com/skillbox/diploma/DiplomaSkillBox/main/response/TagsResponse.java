package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.util.List;

@Data
public class TagsResponse {
    private int count;
    List<TagResponse> tags;
}
