package com.skillbox.diploma.DiplomaSkillBox.main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBasic {

    private Boolean result;
    private String text;
    private final String message;
    private Long id;

    @JsonProperty("errors")
    private ErrorMessage errorMessage;

}
