package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.CaptchaRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Login;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Registration;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CaptchaResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResultResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorListResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorMessage;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.security.jwt.JwtTokenProvider;
import com.skillbox.diploma.DiplomaSkillBox.main.security.jwt.JwtUserFactory;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Data
@Service
@Transactional
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CaptchaRepository captchaRepository;

    private static Map<String, Long> sessions = new HashMap<>();

    public User login(Login login, HttpServletRequest request, HttpServletResponse response) {
        String email = login.getEmail();
        User loggedUser = userRepository.findByEmail(email);

        if (loggedUser != null &&
                bCryptPasswordEncoder.matches(login.getPassword(), loggedUser.getPassword())) {

            log.info("IN login loggedUser {}", loggedUser);

            if (loggedUser != null) {

                Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        login.getEmail(), login.getPassword()));

                String token = jwtTokenProvider.createToken(JwtUserFactory.create(loggedUser));
                sessions.put(token, loggedUser.getId());

                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    Cookie cookie = Arrays.stream(cookies).filter(c -> c.getName().toLowerCase().equals("token"))
                            .findFirst().orElse(null);

                    if (cookie != null){
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }

                Cookie cookie = new Cookie("Token", token);
                cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            return loggedUser;
        }
        return null;
    }

    public void logout() {
        sessions = new HashMap<>();
    }

    public ResponseEntity registration(Registration registrationRequest) {
        String email = registrationRequest.getEmail();
        String name = registrationRequest.getName();
        String password = registrationRequest.getPassword();
        String code = registrationRequest.getCaptcha();
        String captchaSecret = registrationRequest.getCaptchaSecret();

        final String WRONG_EMAIL = "Этот e-mail уже зарегистрирован";
        final String WRONG_NAME = "Имя указано неверно";
        final String WRONG_PASSWORD = "Пароль короче 6-ти символов";
        final String WRONG_CAPTCHA = "Код с картинки введен неверно";

        if (userRepository.findByEmail(email) != null) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setEmail(WRONG_EMAIL);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);
        }

        if (name == null || name.length() > 255) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setEmail(WRONG_NAME);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);
        }

        if (password.length() < 6) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setEmail(WRONG_PASSWORD);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);
        }

        if (code == null || code.length() == 0 ||
                !bCryptPasswordEncoder.matches(code, captchaSecret)) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setEmail(WRONG_CAPTCHA);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);

        }

        User user = new User();
        user.setIsModerator(false);
        user.setEmail(email);
        user.setName(name);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRegTime(Instant.now());
        userRepository.save(user);

        log.info("IN REGISTRATION user {} is registered", user);

        return new ResponseEntity(true, HttpStatus.OK);
    }

    public CaptchaResponse getCaptchaResponse(String secret, String captcha) {
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setSecret(secret);
        captchaResponse.setImage("data:image/jpg;base64," + captcha);
        return captchaResponse;
    }

    public User getCurrentUser(String token) {

        log.info("IN check session {}, token {}", sessions.get(token), token);

        if (token != null && token.length() > 0 && sessions.containsKey(token)) {
            User currentUser = userRepository.findById(sessions.get(token)).orElse(null);

            log.info("IN check currentUser {}", currentUser);

            return currentUser;
        }
        return null;
    }
}
