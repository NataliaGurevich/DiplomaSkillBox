package com.skillbox.diploma.DiplomaSkillBox.main.mapper;

import com.skillbox.diploma.DiplomaSkillBox.main.model.Tag;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TagResponse;
import lombok.Data;

@Data
public class TagMapper {
    private String name;
    private Double weight;

    private TagMapper() {
    }

    public static TagResponse converter (Tag tag, Double weight){
        TagResponse tagResponse = new TagResponse();
        tagResponse.setName(tag.getName());
        tagResponse.setWeight(weight);
        return tagResponse;
    }
}
