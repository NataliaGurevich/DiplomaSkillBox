package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Data
@Component
public class InitializeResponse {

    @Value("${init.title}")
    private String title;

    @Value("${init.subtitle}")
    private String subtitle;

    @Value("${init.phone}")
    private String phone;

    @Value("${init.email}")
    private String email;

    @Value("${init.copyright}")
    private String copyright;

    @Value("${init.copyrightFrom}")
    private String copyrightFrom;
}
