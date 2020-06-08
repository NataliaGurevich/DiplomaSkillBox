package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class ErrorResponse {

    private Boolean result = false;
    private String message;
}
