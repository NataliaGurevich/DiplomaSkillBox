package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.CommentMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.mapper.UserMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.*;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.*;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PostAddRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Slf4j
@Data
@Service
@Transactional
public class PostAddService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private TagToPostRepository tagToPostRepository;

    public ResultResponse addNewPost(PostAddRequest postAddRequest, String token) throws ParseException {

        final String errorTitle = "Заголовок не установлен";
        final String errorText = "Текст публикации слишком короткий";

        Post postCreated;
        User currentUser = authService.getCurrentUser(token);

        boolean isActive = postAddRequest.isActive();
        String title = postAddRequest.getTitle();
        String text = postAddRequest.getText();
        String time = postAddRequest.getTime();
        List<String> tagsName = postAddRequest.getTags();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date result = df.parse(time);
        Instant instant = result.toInstant();

        if (text.length() < 50) {
            return new ResultResponse(false, errorText);

        } else if (title.length() < 3 || title.length() > 255) {
            return new ResultResponse(false, errorTitle);

        } else if (currentUser == null) {
            return new ResultResponse(false, "Current user = NULL");

        } else {
            Post post = new Post();
            post.setIsActive(isActive);
            post.setTitle(title);
            post.setText(text);
            post.setModerationStatus("NEW");
            post.setUser(currentUser);
            post.setTime(instant.isBefore(Instant.now()) ? Instant.now() : instant);
            post.setModerator(null);
            post.setViewCount(0);
            postCreated = postRepository.save(post);

            log.info("NEW POST {}", postCreated);
        }

        Set<Tag> tags = addTag(tagsName);
        if (tags != null && tags.size() > 0) {
            for (Tag tag : tags) {
                TagToPost tagToPost = new TagToPost();
                tagToPost.setPost(postCreated);
                tagToPost.setTag(tag);
                getTagToPostRepository().save(tagToPost);
            }
        }

        return new ResultResponse(true, "");
    }

    public Set<Tag> addTag(List<String> tagsName) {
        Set<String> currentTags = new HashSet<>(tagRepository.findAllTagName());
        Set<Tag> tags = new HashSet<>();

        if (tagsName != null && tagsName.size() > 0) {
            for (String tagItem : tagsName) {
                Tag tag;
                if (!currentTags.contains(tagItem.toUpperCase())) {
                    tag = new Tag();
                    tag.setName(tagItem.toUpperCase());
                    tagRepository.save(tag);
                } else {
                    tag = tagRepository.findIdByName(tagItem.toUpperCase());
                }
                tags.add(tag);
            }
        }
        return tags;
    }
}
