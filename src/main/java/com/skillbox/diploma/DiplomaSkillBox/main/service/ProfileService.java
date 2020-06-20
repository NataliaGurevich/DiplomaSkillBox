package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.ProfileRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorListResponse;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorMessage;
import com.skillbox.diploma.DiplomaSkillBox.main.response.TrueFalseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    @Value("${com.cloudinary.cloud_name}")
    private String cloudName;

    @Value("${com.cloudinari.url}")
    private String cloudUri;

    private final String EMAIL_ERROR = "Этот e-mail уже зарегистрирован";
    private final String PHOTO_ERROR = "Фото слишком большое, нужно не более 5Mb";
    private final String NAME_ERROR = "Имя указано неверно";
    private final String PASSWORD_ERROR = "Пароль короче 6-ти символов";

    @Autowired
    public ProfileService(UserRepository userRepository, FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
    }

    public ResponseEntity changeProfile(MultipartFile photo, String name, String email, String password, int removePhoto, User currentUser) throws IOException {

        ErrorMessage message = new ErrorMessage();
        boolean result = true;

        if (name.equals("")) {
            message.setName(NAME_ERROR);
            result = false;
        }
        if (!email.equals("") && (userRepository.findByEmail(email) != null && !userRepository.findByEmail(email).equals(currentUser))) {
            message.setEmail(EMAIL_ERROR);
            result = false;
        }
        if (!password.equals("") && password.length() < 6) {
            message.setPassword(PASSWORD_ERROR);
            result = false;
        }
        if (photo != null && photo.getBytes().length > 5 * 1024 * 1024) {
            message.setPhoto(PHOTO_ERROR);
            result = false;
        }
        if (!result) {
            return new ResponseEntity(new ErrorListResponse(message), HttpStatus.OK);
        } else {

            String avatar = fileUploadService.fileUploadAvatar(photo);
            if (removePhoto != 1) {
                currentUser.setPhoto(avatar);

            } else {
                currentUser.setPhoto(null);
            }
            currentUser.setName(name);
            currentUser.setEmail(email);

            if (password.length() >= 6) {
                currentUser.setPassword(password);
            }
            userRepository.save(currentUser);
        }
        return new ResponseEntity(new TrueFalseResponse(true), HttpStatus.OK);
    }

    public ResponseEntity changeProfile(ProfileRequest profileRequest, User currentUser) {

        String name = profileRequest.getName();
        String email = profileRequest.getEmail();
        String password = profileRequest.getPassword();
        int removePhoto = profileRequest.getRemovePhoto();

        ErrorMessage message = new ErrorMessage();
        boolean result = true;

        if (name.equals("")) {
            message.setName(NAME_ERROR);
            result = false;
        }
        if (!email.equals("") && (userRepository.findByEmail(email) != null && !userRepository.findByEmail(email).equals(currentUser))) {
            message.setEmail(EMAIL_ERROR);
            result = false;
        }
        if (!StringUtils.isEmpty(password) && password.length() < 6) {
            message.setEmail(PASSWORD_ERROR);
            result = false;
        }

        if (!result) {
            return new ResponseEntity(new ErrorListResponse(message), HttpStatus.OK);
        } else {
            currentUser.setName(name);
            currentUser.setEmail(email);

            if (removePhoto == 1) {
                currentUser.setPhoto(null);
            }

            if (!StringUtils.isEmpty(password)) {
                currentUser.setPassword(password);
            }
            userRepository.save(currentUser);
        }
        return new ResponseEntity(new TrueFalseResponse(true), HttpStatus.OK);
    }
}
