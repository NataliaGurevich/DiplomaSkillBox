package com.skillbox.diploma.DiplomaSkillBox.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "is_moderator", columnDefinition = "false")
    private Boolean isModerator;

    @Temporal(TemporalType.DATE)
    @Column(name = "reg_time", nullable = false)
    private Date regTime;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "code")
    private String code;

    @Column(name = "photo")
    private String photo;

    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts;

    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> postsForModerating;

    @OneToMany(mappedBy = "post_votes", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostVote> likes;

    @OneToMany(mappedBy = "post_comments", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostComment> comments;

    public User() {
        this.isModerator = false;
        this.regTime = new Date();
        posts = new HashSet<>();
        likes = new HashSet<>();
        comments = new HashSet<>();

        if (isModerator) {
            postsForModerating = new HashSet<>();
        }
    }
}
