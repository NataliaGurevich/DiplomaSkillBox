package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.TagMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Post;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.TagRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.TagToPostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TagResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TagsResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final TagToPostRepository tagToPostRepository;

    @Autowired
    public TagService(TagRepository tagRepository, PostRepository postRepository, TagToPostRepository tagToPostRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.tagToPostRepository = tagToPostRepository;
    }

    private List<TagResponse> getListTags(String query) {
        List<TagResponse> tagsWeights = new ArrayList<>();

        int countPosts = postRepository.findCountPosts(Instant.now()).orElse(1);

        List<Tag> tags = null;
        if (query.equals("")) {
            tags = tagRepository.findAll();
        }
        else{
            tags = tagRepository.findTagByQuery(query.toUpperCase() + "%");
        }

        if (tags != null) {
            for (Tag tag : tags) {
                List<Post> posts = tagToPostRepository.findPostsProTag(tag);

                int countPostsProTags = posts == null ? 0 : (int) posts.stream()
                        .filter(p -> p.getModerationStatus().equalsIgnoreCase("ACCEPTED")
                                && p.getIsActive()).count();
                double weight = (double) countPostsProTags / (double) countPosts;

                log.info("TAG {}, TotalPosts {}, PostsProTag {}, weight {}", tag.getName(), countPosts, countPostsProTags, weight);

                tagsWeights.add(TagMapper.converter(tag, weight));
            }
        }
        return tagsWeights;
    }


    public ResponseEntity<TagsResponse> getAllTags(String query) {
        TagsResponse tagsResponse = new TagsResponse();
        List<TagResponse> tagsWeights = getListTags(query);

        tagsResponse.setCount(tagsWeights.size());
        tagsResponse.setTags(tagsWeights);

        log.info("IN TAGS {}", tagsResponse);

        return new ResponseEntity<>(tagsResponse, HttpStatus.OK);
    }
}
