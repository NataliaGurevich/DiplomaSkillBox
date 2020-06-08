package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    User register(User user);

    List<User> getAll();

    User findByUserName(String name);

    User findByUserEmail(String email);

    User FindByUserId(Long id);

    void delete(Long id);
}

