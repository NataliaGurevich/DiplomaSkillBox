package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Table(name = "tag2post")
public class TagToPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column(name = "post_id")
    @Getter
    @Setter
    private Long postId;

    @Column(name = "tag_id")
    @Getter
    @Setter
    private Long tagId;
}
