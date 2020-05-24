package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tag2post")
public class TagToPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tag_id")
    private Tag tag;
}
