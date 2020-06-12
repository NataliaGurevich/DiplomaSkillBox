package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class StatisticResponse {
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private String firstPublication;

}
