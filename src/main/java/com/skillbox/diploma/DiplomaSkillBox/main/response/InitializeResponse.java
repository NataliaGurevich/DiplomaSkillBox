package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;
import org.springframework.stereotype.Service;

@Data
public class InitializeResponse {
    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightFrom;

    public InitializeResponse(){
        title = "DevPub";
        subtitle = "Developer stories";
        phone = "+7 903 666-44-55";
        email = "mail@mail.ru";
        copyright = "Dmitry Sergeev";
        copyrightFrom = "2005";
    }
}
