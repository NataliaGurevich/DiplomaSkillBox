package com.skillbox.diploma.DiplomaSkillBox.main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ResponseBasicResult {

    protected final Boolean result;

    protected ResponseBasicResult(Boolean result) {
        this.result = result;
    }
}
