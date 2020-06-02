package com.skillbox.diploma.DiplomaSkillBox.main.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GlobalSettingsResponse {

    @JsonProperty("multiuser_MODE")
    private boolean MULTIUSER_MODE;

    @JsonProperty("post_PREMODERATION")
    private boolean POST_PREMODERATION;

    @JsonProperty("statistics_IS_PUBLIC")
    private boolean STATISTICS_IS_PUBLIC;
}
