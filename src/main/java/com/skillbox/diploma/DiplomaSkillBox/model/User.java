package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column(name = "is_moderator", columnDefinition = "false")
    @Getter
    @Setter
    private Boolean isModerator;

    @Temporal(TemporalType.DATE)
    @Column(name = "reg_time", nullable = false)
    @Getter
    @Setter
    private Date regTime;

    @Column(name = "name", nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(name = "email", nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(name = "password", nullable = false)
    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String photo;

    public User() {
        this.isModerator = false;
        this.regTime = new Date();
    }
}
