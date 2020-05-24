package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "is_active", nullable = false, columnDefinition = "false")
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status")
    private ModerationStatus moderationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Temporal(TemporalType.DATE)
    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @OneToMany(mappedBy = "post_votes", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostVote> likes;

    @OneToMany(
            mappedBy = "posts",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Tag> tags;

    @OneToMany(mappedBy = "post_comments", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostComment> comments;

    public Post() {
        this.isActive = false;
        this.moderationStatus = ModerationStatus.NEW;
        this.time = new Date();
        this.viewCount = 0;
        likes = new HashSet<>();
        tags = new HashSet<>();
        comments = new HashSet<>();
    }
}
