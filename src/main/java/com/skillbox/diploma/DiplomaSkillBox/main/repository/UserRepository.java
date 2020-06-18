package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByName(String name);
    User findByEmail(String email);
    User findByCode(String code);
    List<User> findAll();

    @Query(value = "SELECT * FROM users WHERE is_moderator=true", nativeQuery = true)
    List<User> findAllModerators();
}
