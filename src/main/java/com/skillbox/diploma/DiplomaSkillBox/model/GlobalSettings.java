package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Table(name = "global_settings")
public class GlobalSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column(name = "code", nullable = false)
    @Getter
    @Setter
    private String code;

    @Column(name = "name", nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(name = "value", nullable = false)
    @Getter
    @Setter
    private Boolean value;
}
