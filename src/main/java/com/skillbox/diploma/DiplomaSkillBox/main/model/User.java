package com.skillbox.diploma.DiplomaSkillBox.main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
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

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "photo")
    private String photo;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
//    private Set<Post> posts;
//
//    @OneToMany(mappedBy = "moderator", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
//    private Set<Post> postsForModerating;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
//    private Set<PostVote> likes;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
//    private Set<PostComment> comments;
}
