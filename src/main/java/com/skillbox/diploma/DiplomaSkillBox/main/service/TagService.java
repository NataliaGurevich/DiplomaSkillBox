package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.TagMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.TagRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TagResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TagsResponse;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Log
@Data
@Service
@Transactional
public class TagService {

    @Autowired
    TagRepository tagRepository;

    private List<TagResponse> getListTags() {
        List<TagResponse> tagsWeights = new ArrayList<>();

        List<Tag> tags = tagRepository.findAll();

        double weight = 0.45; //!query
        if (tags != null) {
            for (Tag tag : tags) {
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

        log.info("[" + tagsResponse.toString() + "]");

        return tagsResponse;
    }
}
