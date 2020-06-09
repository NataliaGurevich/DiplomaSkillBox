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

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagToPostRepository tagToPostRepository;

    private List<TagResponse> getListTags() {
        List<TagResponse> tagsWeights = new ArrayList<>();

        int countPosts = postRepository.findCountPosts(Instant.now()).orElse(1);
        List<Tag> tags = tagRepository.findAll();

        if (tags != null) {
            for (Tag tag : tags) {
                List<Post> posts = tagToPostRepository.findPostsProTag(tag);

                int countPostsProTags = posts == null ? 0 : (int)posts.stream()
                        .filter(p -> p.getModerationStatus().equalsIgnoreCase("ACCEPTED")
                                && p.getIsActive()).count();
                double weight = (double)countPostsProTags / (double)countPosts;

                log.info("TAG {}, TotalPosts {}, PostsProTag {}, weight {}", tag.getName(), countPosts, countPostsProTags, weight);

                tagsWeights.add(TagMapper.converter(tag, weight));
            }
        }
        return tagsWeights;
    }


    public TagsResponse getAllTags() {
        TagsResponse tagsResponse = new TagsResponse();
        List<TagResponse> tagsWeights = getListTags();

        tagsResponse.setCount(tagsWeights.size());
        tagsResponse.setTags(tagsWeights);

        log.info("IN TAGS {}", tagsResponse);

        return tagsResponse;
    }
}
