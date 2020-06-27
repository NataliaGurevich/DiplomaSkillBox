package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.UserMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.GlobalSettingsRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Login;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PasswordRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PasswordRestoreRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Registration;
import com.skillbox.diploma.DiplomaSkillBox.main.response.AuthResponseTrue;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CaptchaResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;
    private final PostRepository postRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final UserRepository userRepository;
    private final CaptchaService captchaService;
    private final UserMapper userMapper;

    @Value("${global.settings.multiuser}")
    private String multiuser;

    @Autowired
    public ApiAuthController(AuthService authService, PostRepository postRepository, GlobalSettingsRepository globalSettingsRepository, UserRepository userRepository, CaptchaService captchaService, UserMapper userMapper) {
        this.authService = authService;
        this.postRepository = postRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.userRepository = userRepository;
        this.captchaService = captchaService;
        this.userMapper = userMapper;
    }

    @GetMapping("/check")
    public ResponseEntity<AuthResponseTrue> getCheck(@CookieValue(value = "Token", defaultValue = "") String token) {

        return new ResponseEntity<>(authService.checkAuth(token), OK);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseTrue> login(@RequestBody Login loginRequest, HttpServletRequest request, HttpServletResponse response) {

        AuthResponseTrue authResponse = authService.loginBySetting(loginRequest, request, response);

        return authResponse != null ?
                new ResponseEntity<>(authResponse, OK)
                :
                new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseBasic> registration(@RequestBody Registration registrationRequest) {

        if (globalSettingsRepository.findSettingsValueByCode(multiuser)) {
            return authService.registration(registrationRequest);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<ResponseBasic> logout(@CookieValue(value = "Token", defaultValue = "") String token) {
        authService.logout(token);
        return new ResponseEntity<>(ResponseBasic.builder().result(true).build(), OK);
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> captcha() throws IOException {
        return captchaService.createCaptcha();
    }

    @PostMapping("/restore")
    public ResponseEntity<ResponseBasic> restorePassword(@RequestBody PasswordRestoreRequest passwordRequest) {

        return authService.sendMailToRestorePassword(passwordRequest);
    }

    @PostMapping("/password")
    public ResponseEntity<ResponseBasic> checkPasswordForRestore(@RequestBody PasswordRequest passwordRequest) {

        return authService.restorePassword(passwordRequest);
    }
}