package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.UserMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
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
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasicResult;
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

    @Value("${global.settings.multiuser}")
    private String multiuser;

    @Autowired
    public ApiAuthController(AuthService authService, PostRepository postRepository, GlobalSettingsRepository globalSettingsRepository, UserRepository userRepository, CaptchaService captchaService) {
        this.authService = authService;
        this.postRepository = postRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.userRepository = userRepository;
        this.captchaService = captchaService;
    }

    @GetMapping("/check")
    public ResponseEntity<ResponseBasicResult> getCheck(@CookieValue(value = "Token", defaultValue = "") String token) {

        User currentUser = authService.getCurrentUser(token);

        log.info("IN getCheck {}, token {}", currentUser, token);

        AuthResponseTrue.AuthResponseTrueBuilder builder = AuthResponseTrue.builder();

        AuthResponseTrue authResponse = currentUser == null ?
                builder.result(false).build()
                :
                AuthResponseTrue.builder().user(UserMapper.converterToFullName(currentUser, currentUser.getIsModerator() ? postRepository.findNewPosts().orElse(0) : 0)).build();

        return new ResponseEntity<>(authResponse, OK);

    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBasicResult> login(@RequestBody Login loginRequest, HttpServletRequest request, HttpServletResponse response) {

        User user = userRepository.findByEmail(loginRequest.getEmail());
        boolean isMultiUser = globalSettingsRepository.findSettingsValueByCode(multiuser);
        boolean isModerator = user != null && user.getIsModerator();

        if ((isMultiUser && user != null) || (!isMultiUser && isModerator)) {
            User loggedUser = authService.login(loginRequest, request, response);

            AuthResponseTrue.AuthResponseTrueBuilder builder = AuthResponseTrue.builder();

            AuthResponseTrue authResponse = loggedUser == null ?
                    builder.result(false).message("Invalid email or password").build()
                    :
                    builder.user(UserMapper.converterToFullName(loggedUser, loggedUser.getIsModerator() ? postRepository.findNewPosts().orElse(0) : 0)).build();

            return new ResponseEntity<>(authResponse, OK);

        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
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