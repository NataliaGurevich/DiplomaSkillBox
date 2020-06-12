package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class ErrorTextResponse {

    private Boolean result = false;
    private ErrorText errors;

    public ErrorTextResponse(ErrorText errors) {
        this.errors = errors;
    }
}
