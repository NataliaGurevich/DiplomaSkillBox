package com.skillbox.diploma.DiplomaSkillBox.main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBasic {

    protected final Boolean result;
    protected final String text;
    protected final String message;
    protected final Long id;

    @JsonProperty("errors")
    protected ErrorMessage errorMessage;


}
