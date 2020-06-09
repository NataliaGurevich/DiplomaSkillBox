package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class ErrorListResponse {

    private Boolean result = false;
    private ErrorMessage errors;

    public ErrorListResponse(ErrorMessage errors) {
        this.errors = errors;
    }
}
