package com.skillbox.diploma.DiplomaSkillBox.main.schedule;

import com.skillbox.diploma.DiplomaSkillBox.main.model.CaptchaCode;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.CaptchaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired
    private CaptchaRepository captchaRepository;

    @Scheduled(cron = "${cron.expression.captcha.del}")
    public void deletingCaptcha(){

        List<CaptchaCode> captchaCodes = captchaRepository.getCaptchaCodeLastHour(Instant.now().minusSeconds(60 * 60));

        if (captchaCodes != null && captchaCodes.size() > 0) {
            for (CaptchaCode captcha : captchaCodes) {
                captchaRepository.delete(captcha);

                log.info("IN SCHEDULE captcha {} is deleted", captcha);
            }
            captchaCodes.clear();
        }
        else {
            log.info("IN SCHEDULE there is no captcha for deleting");
        }
    }
}
