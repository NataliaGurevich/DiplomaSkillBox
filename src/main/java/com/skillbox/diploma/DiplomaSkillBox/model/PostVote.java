package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Temporal(TemporalType.DATE)
    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "value", nullable = false)
    private Boolean value;

    public PostVote() {
        this.time = new Date();
    }
}
