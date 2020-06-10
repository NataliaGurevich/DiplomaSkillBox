package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

import java.util.Set;
import java.util.TreeMap;

@Data
public class CalendarResponse {
    private Set years;
    private TreeMap<String, Integer> posts;

    public CalendarResponse(Set years, TreeMap<String, Integer> posts) {
        this.years = years;
        this.posts = posts;
    }
}
