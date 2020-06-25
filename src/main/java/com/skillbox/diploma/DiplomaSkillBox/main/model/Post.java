package com.skillbox.diploma.DiplomaSkillBox.main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Indexed
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default false")
    private Boolean isActive;

    @Column(name = "moderation_status", nullable = false, columnDefinition = "NEW")
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
    private String moderationStatus;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "time", nullable = false)
    @Field(analyze=Analyze.NO)
    @DateBridge(resolution =Resolution.MILLISECOND)
    @SortableField
    private Instant time;

    @Column(name = "title", nullable = false)
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
    private String title;

    @Column(name = "text", nullable = false)
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
    private String text;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    //
//    @OneToMany(mappedBy = "post",  fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Transient
    private Set<PostVote> likes;

    @Transient
    private Set<PostComment> comments;

    //    @ManyToMany(cascade = {CascadeType.ALL})
//    @JoinTable(name = "tag2post",
//            joinColumns = @JoinColumn(name = "post_id"),
//            inverseJoinColumns = @JoinColumn(name = "tag_id")
//    )
//    @OneToMany(mappedBy = "posts")
    @Transient
    Set<Tag> tagsForPost;

    @Transient
    Integer likeCount;

    @Transient
    Integer commentCount;
}