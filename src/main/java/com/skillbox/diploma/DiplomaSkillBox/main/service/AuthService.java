package com.skillbox.diploma.DiplomaSkillBox.main.service;

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
import com.skillbox.diploma.DiplomaSkillBox.main.response.AuthResponseTrue;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CaptchaResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorMessage;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
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
import org.springframework.web.bind.annotation.RequestBody;

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

    @Value("${global.settings.multiuser}")
    private String multiuser;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CaptchaRepository captchaRepository;
    private final EmailService emailService;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final UserMapper userMapper;
    private Map<String, Long> sessions;
    private Map<String, Instant> codes;


    private final String WRONG_EMAIL = "Этот e-mail уже зарегистрирован";
    private final String WRONG_NAME = "Имя указано неверно";
    private final String WRONG_PASSWORD = "Пароль короче 6-ти символов";
    private final String WRONG_CAPTCHA = "Код с картинки введен неверно";
    private final String WRONG_CODE = "Ссылка для восстановления пароля устарела." +
            "<a href=\"/auth/restore\">Запросить ссылку снова</a>";

    @Autowired
    public AuthService(UserRepository userRepository, PostRepository postRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, CaptchaRepository captchaRepository, Map<String, Long> sessions, EmailService emailService, GlobalSettingsRepository globalSettingsRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.captchaRepository = captchaRepository;
        this.emailService = emailService;
        this.globalSettingsRepository = globalSettingsRepository;
        this.userMapper = userMapper;
        this.sessions = new HashMap<>();
        this.codes = new HashMap<>();
    }

    public AuthResponseTrue checkAuth(String token) {

        User currentUser = getCurrentUser(token);

        log.info("IN getCheck {}, token {}", currentUser, token);

        AuthResponseTrue.AuthResponseTrueBuilder builder =
                AuthResponseTrue.builder();

        return currentUser == null ?
                builder.result(false).build()
                :
                builder.result(true)
                        .user(userMapper.converterToFullName(currentUser))
                        .build();
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

    public AuthResponseTrue loginBySetting(@RequestBody Login loginRequest, HttpServletRequest request, HttpServletResponse response) {

        User user = userRepository.findByEmail(loginRequest.getEmail());
        boolean isMultiUser = globalSettingsRepository.findSettingsValueByCode(multiuser);
        boolean isModerator = user != null && user.getIsModerator();

        if ((isMultiUser && user != null) || (!isMultiUser && isModerator)) {
            User loggedUser = login(loginRequest, request, response);

            AuthResponseTrue.AuthResponseTrueBuilder builder = AuthResponseTrue.builder();

            AuthResponseTrue authResponse = loggedUser == null ?
                    builder.result(false).message("Invalid email or password").build()
                    :
                    builder.result(true).user(userMapper.converterToFullName(loggedUser)).build();

            return authResponse;
        } else {
            return null;
        }
    }

    public void logout(String token) {
        for (String item : sessions.keySet()) {
            if (item.equals(token))
                sessions.remove(token);
        }
    }

    public ResponseEntity<ResponseBasic> registration(Registration registrationRequest) {
        String email = registrationRequest.getEmail();
        String name = registrationRequest.getName();
        String password = registrationRequest.getPassword();
        String code = registrationRequest.getCaptcha();
        String captchaSecret = registrationRequest.getCaptchaSecret();

        boolean result = true;
        boolean isNameError = false;
        boolean isEmailError = false;
        boolean isPasswordError = false;
        boolean isCodeError = false;

        if (userRepository.findByEmail(email) != null) {
            isEmailError = true;
            result = false;
        }

        if (StringUtils.isEmpty(name) || name.length() > 255) {
            isNameError = true;
            result = false;
        }

        if (password.length() < 6) {
            isPasswordError = true;
            result = false;
        }

        if (code == null || code.length() == 0 ||
                !bCryptPasswordEncoder.matches(code, captchaSecret)) {
            isCodeError = true;
            result = false;
        }
        if (!result) {
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .name(isNameError ? WRONG_NAME : null)
                    .email(isEmailError ? WRONG_EMAIL : null)
                    .password(isPasswordError ? WRONG_PASSWORD : null)
                    .captcha(isCodeError ? WRONG_CAPTCHA : null)
                    .build();
            ResponseBasic responseBasic = ResponseBasic.builder().result(false).errorMessage(errorMessage).build();
            return new ResponseEntity(responseBasic, HttpStatus.OK);
        }

        User user = new User();
        user.setIsModerator(false);
        user.setEmail(email);
        user.setName(name);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRegTime(Instant.now());
        userRepository.save(user);

        log.info("IN REGISTRATION user {} is registered", user);

        ResponseBasic responseBasic = ResponseBasic.builder().result(true).build();
        return new ResponseEntity(responseBasic, HttpStatus.OK);
    }

    public CaptchaResponse getCaptchaResponse(String secret, String captcha) {
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setSecret(secret);
        captchaResponse.setImage("data:image/jpg;base64," + captcha);
        return captchaResponse;
    }

    public ResponseEntity<ResponseBasic> sendMailToRestorePassword(PasswordRestoreRequest passwordRequest) {
        String email = passwordRequest.getEmail();
        User user = userRepository.findByEmail(email);
        final int MAXIMUM_LENGTH = 45;

        if (globalSettingsRepository.findSettingsValueByCode("MULTIUSER_MODE") || user.getIsModerator()) {

            if (!StringUtils.isEmpty(email) && user != null) {

                Random random = new Random();
                String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
                StringBuilder generatedCode = new StringBuilder();
                for (int i = 0; i < MAXIMUM_LENGTH; i++) {
                    int randonSequence = random.nextInt(CHARACTERS.length());
                    generatedCode.append(CHARACTERS.charAt(randonSequence));
                }

                host = host.startsWith("MAIL_HOST=") ? host.replaceFirst("MAIL_HOST=", "") : host;
                String code = generatedCode.toString();
                String subj = "Welcome to DevPub!";
                String message = String.format("Hello, %s! \n" +
                                "Welcome to DevPub. Please, visit next link %s/login/change-password/%s to restore password",
                        user.getName(), host, code);

                log.info("CODE {} to {} is sent", code, email);

                boolean result = emailService.sendMessage(email, subj, message);
                if (result) {
                    user.setCode(code);
                    userRepository.save(user);
                    codes.put(code, Instant.now());
                }

                log.info("MAIL TO {} IS SENT {}", user.getName(), result);

                ResponseBasic responseBasic = ResponseBasic.builder().result(result).build();
                return new ResponseEntity(responseBasic, HttpStatus.OK);
            }
            ResponseBasic responseBasic = ResponseBasic.builder().result(false).build();
            return new ResponseEntity(responseBasic, HttpStatus.OK);
        } else {
            ResponseBasic responseBasic = ResponseBasic.builder().result(false).build();
            return new ResponseEntity(responseBasic, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseBasic> restorePassword(PasswordRequest passwordRequest) {

        String password = passwordRequest.getPassword();
        String code = passwordRequest.getCode();
        String captcha = passwordRequest.getCaptcha();
        String captchaSecret = passwordRequest.getCaptchaSecret();

        boolean result = true;
        boolean isCaptchaError = false;
        boolean isPasswordError = false;
        boolean isCodeError = false;

        User currentUser = userRepository.findByCode(code);

        if (password.length() < 6) {
            isPasswordError = true;
            result = false;
        }

        if (StringUtils.isEmpty(captcha) || !bCryptPasswordEncoder.matches(captcha, captchaSecret)) {
            isCaptchaError = true;
            result = false;
        }

        if (currentUser == null) {
            isCodeError = true;
            result = false;
        }

        if (!result) {
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .password(isPasswordError ? WRONG_PASSWORD : null)
                    .code(isCodeError ? WRONG_CODE : null)
                    .captcha(isCaptchaError ? WRONG_CAPTCHA : null)
                    .build();
            ResponseBasic responseBasic = ResponseBasic.builder().result(false).errorMessage(errorMessage).build();
            return new ResponseEntity(responseBasic, HttpStatus.OK);
        } else {

            currentUser.setPassword(bCryptPasswordEncoder.encode(password));
            userRepository.save(currentUser);

            ResponseBasic responseBasic = ResponseBasic.builder().result(true).build();
            return new ResponseEntity(responseBasic, HttpStatus.OK);
        }
    }

    public Map<String, Instant> getCodes() {
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
