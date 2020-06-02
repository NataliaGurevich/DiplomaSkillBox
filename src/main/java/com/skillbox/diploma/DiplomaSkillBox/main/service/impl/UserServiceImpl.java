package com.skillbox.diploma.DiplomaSkillBox.main.service.impl;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@Log4j2
public class UserServiceImpl implements UserService {


    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }


    @Override
    public User register(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRegTime(new Date());
        user.setIsModerator(false);

        User registeredUser = userRepository.save(user);

        log.info("IN register - user: {} successfully registered", user);

        return registeredUser;
    }

    @Override
    public List<User> getAll() {
        List<User> result = userRepository.findAll();

        log.info("IN getAll - user: {} users found", result.size());

        return result;
    }

    @Override
    public User findByUserName(String name) {
        User result = userRepository.findByName(name);

        log.info("IN findByUserName - user: {} found by userName: {}", result, name);

        return result;
    }

    @Override
    public User findByUserEmail(String email) {
        User result = userRepository.findByEmail(email);

        log.info("IN findByEmail - user: {} found by email: {}", result, email);

        return result;
    }

    @Override
    public User FindByUserId(Long id) {
        User result = userRepository.findById(id).orElse(null);

        if (result == null) {
            log.warn("IN findByUserId - user: no found by userId: {}", id);
            return null;
        }

        log.info("IN findByUserId - user: {} found by userId: {}", result, id);

        return result;
    }

    @Override
    public void delete(Long id) {

        userRepository.deleteById(id);

        log.info("IN delete - user with id: {} successfully deleted", id);

    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        log.info("CURRENT_USER - {} -> {}", userRepository.findByEmail(email), email);

        return userRepository.findByEmail(email);
    }
}
