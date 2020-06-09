package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class ResultResponse {

    private Boolean result = false;
    private String message;

    public ResultResponse(Boolean result, String message){
        this.result = result;
        this.message = message;
    }

    public ResultResponse(Boolean result){
        this.result = result;
        this.message = "";
    }

    public ResultResponse(){
    }
}
