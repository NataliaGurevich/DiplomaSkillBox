package com.skillbox.diploma.DiplomaSkillBox.main.response;

import lombok.Data;

@Data
public class AuthResponseBasic<T> {
    private T data;

    public AuthResponseBasic(T data){
        this.data = data;
    }
}
