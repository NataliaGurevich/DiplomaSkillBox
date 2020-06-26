package com.skillbox.diploma.DiplomaSkillBox.main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseTrue extends ResponseBasicResult {
    private final UserResponse user;
    private final String message;

    @Builder
    public AuthResponseTrue(Boolean result, UserResponse user, String message) {
        super(result);
        this.user = user;
        this.message = message;
    }
}
