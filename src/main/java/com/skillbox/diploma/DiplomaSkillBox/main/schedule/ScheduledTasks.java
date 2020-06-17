package com.skillbox.diploma.DiplomaSkillBox.main.schedule;

import com.skillbox.diploma.DiplomaSkillBox.main.model.CaptchaCode;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.CaptchaRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ScheduledTasks {

    private final CaptchaRepository captchaRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired
    public ScheduledTasks(CaptchaRepository captchaRepository, AuthService authService, UserRepository userRepository) {
        this.captchaRepository = captchaRepository;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "${cron.expression.captcha.del}")
    public void deletingCaptcha() {

        List<CaptchaCode> captchaCodes = captchaRepository.getCaptchaCodeLastHour(Instant.now().minusSeconds(60 * 60));

        if (captchaCodes != null && captchaCodes.size() > 0) {
            for (CaptchaCode captcha : captchaCodes) {
                captchaRepository.delete(captcha);

                log.info("IN SCHEDULE captcha {} is deleted", captcha);
            }
            captchaCodes.clear();
        } else {
            log.info("IN SCHEDULE there is no captcha for deleting");
        }
    }

    @Scheduled(cron = "${cron.expression.code.del}")
    public void deletingCode() {
        Map<String, Instant> codes = authService.getCodes();

        if (codes != null && codes.size() > 0) {
            for (String code : codes.keySet()) {
                if (codes.get(code).plus(3, ChronoUnit.HOURS).isBefore(Instant.now())) {
                    User user = userRepository.findByCode(code);
                    if (user != null) {
                        user.setCode(null);
                        userRepository.save(user);
                        log.info("CODE IS DELETED {}", code);
                    }
                    codes.remove(code);
                }
            }
        }
    }
}
