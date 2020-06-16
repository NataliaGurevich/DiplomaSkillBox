package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.CaptchaRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Login;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PasswordRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.PasswordRestoreRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Registration;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CaptchaResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorListResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorMessage;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TrueFalseResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.security.jwt.JwtTokenProvider;
import com.skillbox.diploma.DiplomaSkillBox.main.security.jwt.JwtUserFactory;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Log4j2
@Data
@Service
@Transactional
public class AuthService {

    @Value("${host}")
    private String host;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CaptchaRepository captchaRepository;
    private final EmailService emailService;
    private Map<String, Long> sessions;
    private Map<String, Instant> codes;


    private final String WRONG_EMAIL = "Этот e-mail уже зарегистрирован";
    private final String WRONG_NAME = "Имя указано неверно";
    private final String WRONG_PASSWORD = "Пароль короче 6-ти символов";
    private final String WRONG_CAPTCHA = "Код с картинки введен неверно";
    private final String WRONG_CODE = "Ссылка для восстановления пароля устарела." +
            "<a href=\"/auth/restore\">Запросить ссылку снова</a>";

    @Autowired
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, CaptchaRepository captchaRepository, Map<String, Long> sessions, EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.captchaRepository = captchaRepository;
        this.emailService = emailService;
        this.sessions = new HashMap<>();
        this.codes = new HashMap<>();
    }

    public User login(Login login, HttpServletRequest request, HttpServletResponse response) {
        String email = login.getEmail();
        User loggedUser = userRepository.findByEmail(email);

        if (loggedUser != null &&
                bCryptPasswordEncoder.matches(login.getPassword(), loggedUser.getPassword())) {

            log.info("IN login loggedUser {}", loggedUser);

            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    login.getEmail(), login.getPassword()));

            String token = jwtTokenProvider.createToken(JwtUserFactory.create(loggedUser));
            sessions.put(token, loggedUser.getId());

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Cookie cookie = Arrays.stream(cookies).filter(c -> c.getName().toLowerCase().equals("token"))
                        .findFirst().orElse(null);

                if (cookie != null) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }

            Cookie cookie = new Cookie("Token", token);
            cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
            cookie.setPath("/");
            response.addCookie(cookie);

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

        if (userRepository.findByEmail(email) != null) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setEmail(WRONG_EMAIL);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);
        }

        if (name == null || name.length() > 255) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setName(WRONG_NAME);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);
        }

        if (password.length() < 6) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setPassword(WRONG_PASSWORD);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);
        }

        if (code == null || code.length() == 0 ||
                !bCryptPasswordEncoder.matches(code, captchaSecret)) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setCaptcha(WRONG_CAPTCHA);
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

        return new ResponseEntity(new TrueFalseResponse(true), HttpStatus.OK);
    }

    public CaptchaResponse getCaptchaResponse(String secret, String captcha) {
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setSecret(secret);
        captchaResponse.setImage("data:image/jpg;base64," + captcha);
        return captchaResponse;
    }

    public TrueFalseResponse sendMailToRestorePassword(PasswordRestoreRequest passwordRequest) {

        String email = passwordRequest.getEmail();
        User user = userRepository.findByEmail(email);
        final int MAXIMUM_LENGTH = 45;

        if (!StringUtils.isEmpty(email) && user != null) {

            Random random = new Random();
            String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
            StringBuilder generatedCode = new StringBuilder();
            for (int i = 0; i < MAXIMUM_LENGTH; i++) {
                int randonSequence = random.nextInt(CHARACTERS.length());
                generatedCode.append(CHARACTERS.charAt(randonSequence));
            }

            String code = generatedCode.toString();
            String subj = "Welcome to DevPub!";
            String message = String.format("Hello, %s! \n" +
                    "Welcome to DevPub. Please, visit next link %s/login/change-password/%s to restore password",
                    user.getName(), host, code);

            log.info("CODE {} to {} is sent", code, email);

            boolean result = emailService.sendMessage(email, subj, message);
            if (result){
                user.setCode(code);
                userRepository.save(user);
                codes.put(code, Instant.now());
            }

            log.info("MAIL TO {} IS SENT {}", user.getName(), result);

            return new TrueFalseResponse(result);
        }
        return new TrueFalseResponse(false);
    }

    public ResponseEntity restorePassword(PasswordRequest passwordRequest) {

        String password = passwordRequest.getPassword();
        String code = passwordRequest.getCode();
        String captcha = passwordRequest.getCaptcha();
        String captchaSecret = passwordRequest.getCaptchaSecret();

        User currentUser = userRepository.findByCode(code);

        if (password.length() < 6) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setPassword(WRONG_PASSWORD);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);
        }

        if (StringUtils.isEmpty(captcha) || !bCryptPasswordEncoder.matches(captcha, captchaSecret)) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setCaptcha(WRONG_CAPTCHA);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);
        }

        if (currentUser != null) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setCaptcha(WRONG_CODE);
            return new ResponseEntity(new ErrorListResponse(errorMessage), HttpStatus.OK);
        }

        currentUser.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(currentUser);

        return new ResponseEntity(new TrueFalseResponse(true), HttpStatus.OK);
    }

    public Map<String, Instant> getCodes(){
        return codes;
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
