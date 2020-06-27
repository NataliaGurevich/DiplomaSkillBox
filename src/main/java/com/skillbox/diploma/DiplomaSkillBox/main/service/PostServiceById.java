package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.*;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.*;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PostAddRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceById {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostVoteRepository postVoteRepository;
    private final TagToPostRepository tagToPostRepository;
    private final GlobalSettingsRepository globalSettingsRepository;

    @Value("${global.settings.premoderation}")
    private String premoderation;

    @Autowired
    public PostServiceById(PostRepository postRepository, TagRepository tagRepository, PostCommentRepository postCommentRepository, PostVoteRepository postVoteRepository, TagToPostRepository tagToPostRepository, GlobalSettingsRepository globalSettingsRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.postCommentRepository = postCommentRepository;
        this.postVoteRepository = postVoteRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public PostCommentsResponse getPostById(Long id) {

        Post post = postRepository.findById(id).orElse(null);
        PostCommentsResponse postCommentsResponse = null;

        if (post != null) {
            int likeCount = postVoteRepository.findCountLikes(post.getId()).orElse(0);
            int disLikeCount = postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            int commentCount = postCommentRepository.findCountComments(post.getId()).orElse(0);

            int viewCount = post.getViewCount() == null ? 1 : post.getViewCount() + 1;
            post.setViewCount(viewCount);
            postRepository.save(post);

            List<PostComment> postComments = postCommentRepository.findAllByPost(post);

            List<Tag> tags = tagToPostRepository.findByPost(post);
            List<String> tagsName = tags.stream().map(t -> t.getName()).collect(Collectors.toList());

            postCommentsResponse = PostMapper.converterPostWithComment(post, likeCount,
                    disLikeCount, commentCount, postComments, tagsName);
        }

        return postCommentsResponse;
    }

    public ResponseBasic editPostById(Long id, PostAddRequest postAddRequest, User currentUser) throws ParseException {

        Post post = postRepository.findById(id).orElse(null);

        List<Tag> tagToPost = tagToPostRepository.findByPost(post);

        for (Tag tag : tagToPost) {
            List<TagToPost> tagToPosts = tagToPostRepository.findTagToPost(tag.getId(), post.getId());
            for (TagToPost t : tagToPosts) {
                tagToPostRepository.delete(t);
            }
        }

        final String errorTitle = "Заголовок не установлен";
        final String errorText = "Текст публикации слишком короткий";

        boolean isTextError = false;
        boolean isTitleError = false;
        boolean result = true;

        if (post != null) {
            Post postCreated;

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
                post.setIsActive(isActive);
                post.setTitle(title);
                post.setText(text);
                if (post.getUser().equals(currentUser)) {
                    post.setModerationStatus(globalSettingsRepository.findSettingsValueByCode(premoderation) ?
                            "NEW" : "ACCEPTED");
                    post.setModerator(null);
                }
                post.setTime(instant.isBefore(Instant.now()) ? Instant.now() : instant);
                postCreated = postRepository.save(post);

                log.info("NEW POST {}", postCreated);

                Set<Tag> tags = addTag(tagsName);
                if (tags != null && tags.size() > 0) {
                    for (Tag tag : tags) {
                        TagToPost tagToPostEdit = new TagToPost();
                        tagToPostEdit.setPost(postCreated);
                        tagToPostEdit.setTag(tag);
                        tagToPostRepository.save(tagToPostEdit);
                    }
                }
                ResponseBasic responseBasic = ResponseBasic
                        .builder()
                        .result(true)
                        .build();
                return responseBasic;
            }
        } else {
            ResponseBasic responseBasic = ResponseBasic
                    .builder()
                    .result(false)
                    .message("Post = null")
                    .build();
            return responseBasic;
        }
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

    private List<PostResponse> cretePostList(List<Post> postList) {
        List<PostResponse> posts = new ArrayList<>();

        for (Post post : postList) {
            int likeCount = postVoteRepository.findCountLikes(post.getId()).orElse(0);
            int disLikeCount = postVoteRepository.findCountDislikes(post.getId()).orElse(0);
            int commentCount = postCommentRepository.findCountComments(post.getId()).orElse(0);
            posts.add(PostMapper.converter(post, likeCount, disLikeCount, commentCount));
        }
        return posts;
    }

    public PostsResponse getAllPostResponse(long count, List<PostResponse> posts) {

        PostsResponse postsResponse = new PostsResponse();
        postsResponse.setCount(count);
        postsResponse.setPosts(posts);

        log.info("POSTS {}", postsResponse);

        return postsResponse;
    }
}
