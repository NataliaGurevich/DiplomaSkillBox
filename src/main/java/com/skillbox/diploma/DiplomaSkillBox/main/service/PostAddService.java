package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.model.TagToPost;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.*;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PostAddRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorMessage;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Data
@Service
@Transactional
public class PostAddService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final TagToPostRepository tagToPostRepository;
    private final GlobalSettingsRepository globalSettingsRepository;

    @Value("${global.settings.premoderation}")
    private String premoderation;

    @Autowired
    public PostAddService(PostRepository postRepository, PostCommentRepository postCommentRepository, TagRepository tagRepository, UserRepository userRepository, AuthService authService, TagToPostRepository tagToPostRepository, GlobalSettingsRepository globalSettingsRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.tagToPostRepository = tagToPostRepository;
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public ResponseBasic addNewPost(PostAddRequest postAddRequest, String token) throws ParseException {

        final String errorTitle = "Заголовок не установлен";
        final String errorText = "Текст публикации слишком короткий";

        boolean isTextError = false;
        boolean isTitleError = false;
        boolean result = true;

        Post postCreated;
        User currentUser = authService.getCurrentUser(token);

        boolean isActive = postAddRequest.isActive();
        String title = postAddRequest.getTitle();
        String text = postAddRequest.getText();
        String time = postAddRequest.getTime();
        List<String> tagsName = postAddRequest.getTags();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date resultDate = df.parse(time);
        Instant instant = resultDate.toInstant();

        if (text.length() < 50) {
            result = false;
            isTextError = true;
        }
        if (title.length() < 3 || title.length() > 255) {
            result = false;
            isTitleError = true;
        }
        if (currentUser == null) {
            ResponseBasic responseBasic = ResponseBasic
                    .builder()
                    .result(false)
                    .build();
            return responseBasic;

        }
        if (!result) {
            ErrorMessage errorMessage = ErrorMessage
                    .builder()
                    .text(isTextError ? errorText : null)
                    .title(isTitleError ? errorTitle : null)
                    .build();
            ResponseBasic responseBasic = ResponseBasic
                    .builder()
                    .result(false)
                    .errorMessage(errorMessage)
                    .build();
            return responseBasic;
        } else {
            Post post = new Post();
            post.setIsActive(isActive);
            post.setTitle(title);
            post.setText(text);

            post.setModerationStatus(globalSettingsRepository.findSettingsValueByCode(premoderation) ?
                    "NEW" : "ACCEPTED");

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
                tagToPostRepository.save(tagToPost);
            }
        }

        ResponseBasic responseBasic = ResponseBasic
                .builder()
                .result(true)
                .build();
        return responseBasic;
    }

    public Set<Tag> addTag(List<String> tagsName) {
        Set<String> currentTags = new HashSet<>(tagRepository.findAllTagName());
        Set<Tag> tags = new HashSet<>();

        if (tagsName != null && tagsName.size() > 0) {
            for (String tagItem : tagsName) {
                if (!StringUtils.isEmpty(tagItem)) {
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
        }
        return tags;
    }
}
