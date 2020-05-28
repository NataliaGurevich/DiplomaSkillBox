package com.skillbox.diploma.DiplomaSkillBox.main.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "is_active", nullable = false, columnDefinition = "false")
    private Boolean isActive;

    @Column(name = "moderation_status", nullable = false, columnDefinition = "NEW")
    private String moderationStatus;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
//    private Set<PostVote> likes;
//
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
//    private Set<PostComment> comments;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "tag2post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    Set<Tag> tags;
}
