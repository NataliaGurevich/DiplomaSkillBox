package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.skillbox.diploma.DiplomaSkillBox.main.model.CaptchaCode;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.CaptchaRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CaptchaResponse;
import org.imgscalr.Scalr;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

import static org.springframework.http.HttpStatus.OK;

@Service
public class CaptchaService {
    final int CAPTCHA_WIDTH = 100;
    final int CAPTCHA_HEIGHT = 35;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthService authService;
    private final CaptchaRepository captchaRepository;

    public CaptchaService(BCryptPasswordEncoder bCryptPasswordEncoder, PostRepository postRepository, AuthService authService, CaptchaRepository captchaRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authService = authService;
        this.captchaRepository = captchaRepository;
    }

    public ResponseEntity<CaptchaResponse> createCaptcha() throws IOException {

        String token = Integer.toString((int) (Math.random() * 100_000) + (int) Math.random() * 100_000);
        String secret = bCryptPasswordEncoder.encode(token);

        Cage cage = new GCage();
        BufferedImage image = cage.drawImage(token);
        image = Scalr.resize(image, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        byte[] imageBytes = bos.toByteArray();
        String imageString = Base64.getEncoder().encodeToString(imageBytes);

        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(Instant.now());
        captchaCode.setCode(token);
        captchaCode.setSecretCode(secret);
        captchaRepository.save(captchaCode);

        return new ResponseEntity(authService.getCaptchaResponse(secret, imageString), OK);
    }
}
