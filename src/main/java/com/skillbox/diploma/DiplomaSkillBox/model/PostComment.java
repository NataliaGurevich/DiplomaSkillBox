package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column(name = "parent_id")
    @Getter
    @Setter
    private Long parentId;

    @Column(name = "post_id", nullable = false)
    @Getter
    @Setter
    private Long postId;

    @Column(name = "user_id", nullable = false)
    @Getter
    @Setter
    private Long userId;

    @Temporal(TemporalType.DATE)
    @Column(name = "time", nullable = false)
    @Getter
    @Setter
    private Date time;

    @Column(name = "text", nullable = false)
    @Getter
    @Setter
    private String text;

    public PostComment() {
        this.time = new Date();
    }
}
