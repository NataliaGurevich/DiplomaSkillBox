package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column(name = "user_id")
    @Getter
    @Setter
    private Long userId;

    @Column(name = "post_id")
    @Getter
    @Setter
    private Long postId;

    @Temporal(TemporalType.DATE)
    @Column(name = "time", nullable = false)
    @Getter
    @Setter
    private Date time;

    @Column(name = "value", nullable = false)
    @Getter
    @Setter
    private Boolean value;

    public PostVote() {
        this.time = new Date();
    }
}
