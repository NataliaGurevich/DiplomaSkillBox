package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {

    List<GlobalSettings> findAll();
}
