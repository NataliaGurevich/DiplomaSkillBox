package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(
            mappedBy = "tags",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    Set<Post> posts;

    public Tag() {
        posts = new HashSet<>();
    }
}
