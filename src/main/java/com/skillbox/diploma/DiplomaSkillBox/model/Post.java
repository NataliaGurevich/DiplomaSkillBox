package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column(name = "is_active", nullable = false, columnDefinition = "false")
    @Getter
    @Setter
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status")
    @Getter
    @Setter
    private ModerationStatus moderationStatus;

    @Column(name = "moderator_id")
    @Getter
    @Setter
    private Long moderatorId;

    @Temporal(TemporalType.DATE)
    @Column(name = "time", nullable = false)
    @Getter
    @Setter
    private Date time;

    @Column(name = "title", nullable = false)
    @Getter
    @Setter
    private String title;

    @Column(name = "text", nullable = false)
    @Getter
    @Setter
    private String text;

    @Column(name = "view_count", nullable = false)
    @Getter
    @Setter
    private Integer viewCount;

    public Post() {
        this.isActive = false;
        this.moderationStatus = ModerationStatus.NEW;
        this.time = new Date();
        this.viewCount = 0;
    }
}
