package com.skillbox.diploma.DiplomaSkillBox.main.response;


import lombok.Getter;

public class BaseResponse {

    @Getter
    private final String status;

    @Getter
    private final Integer code;

    public BaseResponse(String status, Integer code) {
        this.status = status;
        this.code = code;
    }
}
