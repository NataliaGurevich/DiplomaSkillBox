package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.UserMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.AuthResponse;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log
@Data
@Service
@Transactional
public class AuthService {

    @Autowired
    UserRepository userRepository;

    public AuthResponse check() {
        User currentUser = userRepository.findByName("Moderator");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setResult(currentUser.getIsModerator());
        authResponse.setUser(UserMapper.converterToFullName(currentUser));

        log.info("[" + authResponse.toString() + "]");

        return authResponse;
    }
}
