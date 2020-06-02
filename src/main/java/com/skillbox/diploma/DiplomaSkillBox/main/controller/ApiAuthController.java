package com.skillbox.diploma.DiplomaSkillBox.main.controller;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.skillbox.diploma.DiplomaSkillBox.main.mapper.UserMapper;
import com.skillbox.diploma.DiplomaSkillBox.main.model.CaptchaCode;
import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.CaptchaRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Login;
import com.skillbox.diploma.DiplomaSkillBox.main.request.Registration;
import com.skillbox.diploma.DiplomaSkillBox.main.response.AuthResponseTrue;
import com.skillbox.diploma.DiplomaSkillBox.main.response.CaptchaResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CaptchaRepository captchaRepository;

//    @Autowired
//    private AuthenticationManager authenticationManager;

//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

//    private static Map<String, Long> sessions = new HashMap<>();

    @GetMapping("/check")
    public ResponseEntity getCheck(HttpServletRequest request, HttpServletResponse response) {
        User currentUser = authService.getCurrentUser(request);
        if (currentUser != null) {
            AuthResponseTrue authResponse = new AuthResponseTrue();
            authResponse.setUser(UserMapper.converterToFullName(currentUser));
            return new ResponseEntity(authResponse, OK);
        }
        ErrorResponse error = new ErrorResponse();
        error.setMessage("Current user was not found");
        return new ResponseEntity(error, OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Login loginRequest,
                                HttpServletRequest request, HttpServletResponse response) {

        User loggedUser = authService.login(loginRequest, request, response);

        if (loggedUser != null) {

//            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                    loginRequest.getEmail(), loginRequest.getPassword()));
//
//            String token = jwtTokenProvider.createToken(JwtUserFactory.create(loggedUser));
//            sessions.put(token, loggedUser.getId());
//
//            Cookie cookie = new Cookie("Token", token);
//            response.addCookie(cookie);

            AuthResponseTrue authResponse = new AuthResponseTrue();
            authResponse.setUser(UserMapper.converterToFullName(loggedUser));
            return new ResponseEntity(authResponse, OK);
        }
        ErrorResponse error = new ErrorResponse();
        error.setMessage("Invalid email or password");
        return new ResponseEntity(error, OK);
    }

    @PostMapping("/register")
    public ResponseEntity registration(@RequestBody Registration registrationRequest) {

        return authService.registration(registrationRequest);
    }

    @GetMapping("/logout")
    public RedirectView logout() {
        authService.logout();
        return new RedirectView("/");
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> captcha() throws IOException {
        final int CAPTCHA_WIDTH = 100;
        final int CAPTCHA_HEIGHT = 35;

        String token = Integer.toString((int) (Math.random() * 100_000));
        String secret = bCryptPasswordEncoder.encode(token);

        Cage cage = new GCage();
        BufferedImage image = cage.drawImage(token);
        image = Scalr.resize(image, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        byte[] imageBytes = bos.toByteArray();
        String imageString = Base64.getEncoder().encodeToString(imageBytes);

        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(new Date());
        captchaCode.setCode(token);
        captchaCode.setSecretCode(secret);
        captchaRepository.save(captchaCode);

        return new ResponseEntity(authService.getCaptchaResponse(secret, imageString), OK);
    }
}