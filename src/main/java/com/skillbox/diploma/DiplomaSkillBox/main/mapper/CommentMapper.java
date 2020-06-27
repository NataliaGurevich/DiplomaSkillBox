package com.skillbox.diploma.DiplomaSkillBox.main.mapper;

import com.skillbox.diploma.DiplomaSkillBox.main.model.PostComment;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CommentResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TagResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.UserIdNamePhotoResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

@Data
@Component
public class CommentMapper {
    private Long id;
    private String time;
    private String text;
    private UserIdNamePhotoResponse user;

    private final UserMapper userMapper;

    @Autowired
    public CommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public CommentResponse converter(PostComment comment) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM)
                        .withZone( ZoneId.systemDefault() );

        Instant instant = comment.getTime();
        String output = formatter.format( instant );

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
//        commentResponse.setTime(output);
        commentResponse.setTime(LocalDateTime.ofInstant(comment.getTime(), ZoneId.systemDefault()));
        commentResponse.setText(comment.getText());
        commentResponse.setUser(userMapper.converterToNameIdNamePhoto(comment.getUser()));

        return commentResponse;
    }
}
