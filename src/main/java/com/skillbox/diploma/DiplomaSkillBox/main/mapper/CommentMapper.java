package com.skillbox.diploma.DiplomaSkillBox.main.mapper;

import com.skillbox.diploma.DiplomaSkillBox.main.model.PostComment;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CommentResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TagResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.UserIdNamePhotoResponse;
import lombok.Data;

import java.util.Date;

@Data
public class CommentMapper {
    private Long id;
    private Date time;
    private String text;
    private UserIdNamePhotoResponse user;

    private CommentMapper() {
    }

    public static CommentResponse converter(PostComment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setTime(comment.getTime());
        commentResponse.setText(comment.getText());
        commentResponse.setUser(UserMapper.converterToNameIdNamePhoto(comment.getUser()));

        return commentResponse;
    }
}
