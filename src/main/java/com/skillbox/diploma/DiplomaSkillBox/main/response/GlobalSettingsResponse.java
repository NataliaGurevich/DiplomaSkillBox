package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class GlobalSettingsResponse {

    private boolean MULTIUSER_MODE;
    private boolean POST_PREMODERATION;
    private boolean STATISTICS_IS_PUBLIC;
}
