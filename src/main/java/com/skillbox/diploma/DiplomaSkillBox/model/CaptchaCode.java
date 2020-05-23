package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "time", nullable = false)
    @Getter
    @Setter
    private Date time;

    @Column(name = "code", nullable = false)
    @Getter
    @Setter
    private String code;

    @Column(name = "secret_code", nullable = false)
    @Getter
    @Setter
    private String secretCode;
}
