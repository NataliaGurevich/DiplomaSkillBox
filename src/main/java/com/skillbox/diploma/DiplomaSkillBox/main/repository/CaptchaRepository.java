package com.skillbox.diploma.DiplomaSkillBox.main.repository;

import com.skillbox.diploma.DiplomaSkillBox.main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CaptchaRepository extends JpaRepository<CaptchaCode, Long> {

    @Query(value = "SELECT secret_code FROM captcha_codes WHERE code=?1", nativeQuery = true)
    String findCaptchaCodeBySecretCode(String code);

    CaptchaCode save(CaptchaCode captchaCode);

    @Query(value = "SELECT MAX(id) FROM captcha_codes", nativeQuery = true)
    Long getLastId();
}
