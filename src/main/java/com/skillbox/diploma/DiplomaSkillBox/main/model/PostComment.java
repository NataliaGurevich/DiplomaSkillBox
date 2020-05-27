package com.skillbox.diploma.DiplomaSkillBox.main.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private PostComment parent;

//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<PostComment> commentToParent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Temporal(TemporalType.DATE)
    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "text", nullable = false)
    private String text;
}
