package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.skillbox.diploma.DiplomaSkillBox.main.mapper.UserMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.CaptchaRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.GlobalSettingsRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.PostRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Login;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PasswordRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PasswordRestoreRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Registration;
import com.skillbox.diploma.DiplomaSkillBox.main.response.*;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.CaptchaService;
import com.skillbox.diploma.DiplomaSkillBox.main.service.PostServiceByMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final CaptchaRepository captchaRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PostRepository postRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final PostServiceByMode postServiceByMode;
    private final UserRepository userRepository;
    private final CaptchaService captchaService;

    @Value("${global.settings.multiuser}")
    private String multiuser;

    @Autowired
    public ApiAuthController(AuthService authService, CaptchaRepository captchaRepository, BCryptPasswordEncoder bCryptPasswordEncoder, PostRepository postRepository, GlobalSettingsRepository globalSettingsRepository, PostServiceByMode postServiceByMode, UserRepository userRepository, CaptchaService captchaService) {
        this.authService = authService;
        this.captchaRepository = captchaRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.postRepository = postRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.postServiceByMode = postServiceByMode;
        this.userRepository = userRepository;
        this.captchaService = captchaService;
    }

    @GetMapping("/check")
    public ResponseEntity getCheck(@CookieValue(value = "Token", defaultValue = "") String token) {

        log.info("IN getCheck token {}", token);

        User currentUser = authService.getCurrentUser(token);

        log.info("IN getCheck {}, token {}", currentUser, token);

        if (currentUser != null) {
            AuthResponseTrue authResponse = new AuthResponseTrue();
            int count = postRepository.findNewPosts().orElse(0);
            authResponse.setUser(UserMapper.converterToFullName(currentUser, currentUser.getIsModerator() ? count : 0));
            return new ResponseEntity(authResponse, OK);
        }
        ResultResponse error = new ResultResponse();
        error.setMessage("Current user was not found");
        return new ResponseEntity(error, OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Login loginRequest,
                                HttpServletRequest request, HttpServletResponse response) {

        User user = userRepository.findByEmail(loginRequest.getEmail());
        boolean isMultiUser = globalSettingsRepository.findSettingsValueByCode(multiuser);
        boolean isModerator = user != null && user.getIsModerator();

        if ((isMultiUser && user != null) || (!isMultiUser && isModerator)) {

            User loggedUser = authService.login(loginRequest, request, response);

            if (loggedUser != null) {
                AuthResponseTrue authResponse = new AuthResponseTrue();
                int count = postRepository.findNewPosts().orElse(0);
                authResponse.setUser(UserMapper.converterToFullName(loggedUser, loggedUser.getIsModerator() ? count : 0));
                return new ResponseEntity(authResponse, OK);
            }
            else {

                ResultResponse error = new ResultResponse();
                error.setMessage("Invalid email or password");
                return new ResponseEntity(error, OK);
            }

        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity registration(@RequestBody Registration registrationRequest) {

        if (globalSettingsRepository.findSettingsValueByCode(multiuser)) {
            return authService.registration(registrationRequest);
        } else {
            return new ResponseEntity(null, HttpStatus.OK);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity logout(@CookieValue(value = "Token", defaultValue = "") String token) {
        authService.logout(token);
        return new ResponseEntity(new TrueFalseResponse(true), OK);
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