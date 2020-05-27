package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class TagResponse implements Comparable<TagResponse>{
    private String name;
    private Double weight;

    @Override
    public int compareTo(TagResponse o) {
        return this.weight.compareTo(o.getWeight());
    }
}
