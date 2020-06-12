package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.PostMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.*;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.*;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PostAddRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostCommentsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.PostsResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private TagToPostRepository tagToPostRepository;

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

    public ResultResponse editPostById(Long id, PostAddRequest postAddRequest, User currentUser) throws ParseException {

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

        if (post != null) {
            Post postCreated;

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

            } else {
                post.setIsActive(isActive);
                post.setTitle(title);
                post.setText(text);
                if(post.getUser().equals(currentUser)) {
                    post.setModerationStatus("NEW");
                    post.setModerator(null);
                }
                post.setTime(instant.isBefore(Instant.now()) ? Instant.now() : instant);
//                post.setViewCount(0);
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
                return new ResultResponse(true, "");
            }
        }
        else {
            return new ResultResponse(false, "Post = null");
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
