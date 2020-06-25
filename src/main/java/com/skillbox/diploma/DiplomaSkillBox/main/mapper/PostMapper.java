package com.skillbox.diploma.DiplomaSkillBox.main.mapper;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.PostComment;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CommentResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostByTagResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostCommentsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostResponse;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostMapper {

    private PostMapper() {
    }

    public static PostResponse converter(Post post, int likeCount, int dislikeCount, int commentCount) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withZone(ZoneId.systemDefault());

        Instant instant = post.getTime();
        String output = formatter.format(instant);

        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.setTime(output);
        postResponse.setUser(UserMapper.converterToShortName(post.getUser()));
        postResponse.setViewCount(post.getViewCount());
        postResponse.setTitle(post.getTitle());

        String text = post.getText();
        text = StringUtils.isEmpty(Jsoup.parse(text).text()) ? text : Jsoup.parse(text).text();
        text = text.length() < 150 ? text : text.substring(0, 150);
        postResponse.setAnnounce(text);
        postResponse.setLikeCount(likeCount);
        postResponse.setDislikeCount(dislikeCount);
        postResponse.setCommentCount(commentCount);

        return postResponse;
    }

    public static PostCommentsResponse converterPostWithComment(Post post, int likeCount, int dislikeCount, int commentCount,
                                                                List<PostComment> comments, List<String> tags) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withZone(ZoneId.systemDefault());

        Instant instant = post.getTime();
        String output = formatter.format(instant);

        PostCommentsResponse postCommentsResponse = new PostCommentsResponse();
        postCommentsResponse.setId(post.getId());
        postCommentsResponse.setTime(output);
        postCommentsResponse.setUser(UserMapper.converterToShortName(post.getUser()));
        postCommentsResponse.setViewCount(post.getViewCount());
        postCommentsResponse.setTitle(post.getTitle());

        String text = post.getText();
        text = StringUtils.isEmpty(Jsoup.parse(text).text()) ? text : Jsoup.parse(text).text();
        text = text.length() < 150 ? text : text.substring(0, 150);
        postCommentsResponse.setAnnounce(text);
        postCommentsResponse.setText(post.getText());
        postCommentsResponse.setLikeCount(likeCount);
        postCommentsResponse.setDislikeCount(dislikeCount);
        postCommentsResponse.setCommentCount(commentCount);

        List<CommentResponse> commentResponses = new ArrayList<>();
        if (comments != null && comments.size() > 0) {
            for (PostComment comment : comments) {
                CommentResponse commentResponse = CommentMapper.converter(comment);
                commentResponses.add(commentResponse);
            }
        }
        postCommentsResponse.setComments(commentResponses);

        postCommentsResponse.setTags(tags);

        return postCommentsResponse;
    }

    public static PostByTagResponse converterPostByTag(Post post, int likeCount, int dislikeCount, int commentCount,
                                                       List<PostComment> comments) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withZone(ZoneId.systemDefault());

        Instant instant = post.getTime();
        String output = formatter.format(instant);

        PostByTagResponse postByTagResponse = new PostByTagResponse();
        postByTagResponse.setId(post.getId());
        postByTagResponse.setTime(output);
        postByTagResponse.setUser(UserMapper.converterToShortName(post.getUser()));
        postByTagResponse.setViewCount(post.getViewCount());
        postByTagResponse.setTitle(post.getTitle());

        String text = post.getText();
        text = StringUtils.isEmpty(Jsoup.parse(text).text()) ? text : Jsoup.parse(text).text();
        text = text.length() < 150 ? text : text.substring(0, 150);
        postByTagResponse.setAnnounce(text);
        postByTagResponse.setLikeCount(likeCount);
        postByTagResponse.setDislikeCount(dislikeCount);
        postByTagResponse.setCommentCount(commentCount);

        List<CommentResponse> commentResponses = new ArrayList<>();
        if (comments != null && comments.size() > 0) {
            for (PostComment comment : comments) {
                CommentResponse commentResponse = CommentMapper.converter(comment);
                commentResponses.add(commentResponse);
            }
        }
        postByTagResponse.setComments(commentResponses);

        return postByTagResponse;
    }
}
