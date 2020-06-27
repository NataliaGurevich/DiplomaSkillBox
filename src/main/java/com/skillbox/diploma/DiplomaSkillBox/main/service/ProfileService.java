package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.User;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.UserRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.ProfileRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorMessage;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${com.cloudinary.cloud_name}")
    private String cloudName;

    @Value("${com.cloudinari.url}")
    private String cloudUri;

    private final String EMAIL_ERROR = "Этот e-mail уже зарегистрирован / или не введен";
    private final String PHOTO_ERROR = "Фото слишком большое, нужно не более 5Mb";
    private final String NAME_ERROR = "Имя указано неверно";
    private final String PASSWORD_ERROR = "Пароль короче 6-ти символов";

    @Autowired
    public ProfileService(UserRepository userRepository, FileUploadService fileUploadService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseBasic changeProfile(MultipartFile photo, String name, String email, String password, int removePhoto, User currentUser) throws IOException {

        boolean result = true;
        boolean isNameError = false;
        boolean isEmailError = false;
        boolean isPasswordError = false;
        boolean isPhotoError = false;

        User savedUser;

        if (StringUtils.isEmpty(name)) {
            isNameError = true;
            result = false;
        }
        if (StringUtils.isEmpty(email) || (userRepository.findByEmail(email) != null && !userRepository.findByEmail(email).equals(currentUser))) {
            isEmailError = true;
            result = false;
        }
        if (!StringUtils.isEmpty(password) && password.length() < 6) {
            isPasswordError = true;
            result = false;
        }
        if (photo != null && photo.getBytes().length > 5 * 1024 * 1024) {
            isPhotoError = true;
            result = false;
        }
        if (!result) {
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .name(isNameError ? NAME_ERROR : null)
                    .email(isEmailError ? EMAIL_ERROR : null)
                    .password(isPasswordError ? PASSWORD_ERROR : null)
                    .photo(isPhotoError ? PHOTO_ERROR : null)
                    .build();
            ResponseBasic responseBasic = ResponseBasic.builder().result(false).errorMessage(errorMessage).build();
            return responseBasic;
        } else {

            String avatar = fileUploadService.fileUploadAvatar(photo);
            if (removePhoto != 1) {
                currentUser.setPhoto(avatar);

            } else {
                currentUser.setPhoto(null);
            }
            currentUser.setName(name);
            currentUser.setEmail(email);

            if (!StringUtils.isEmpty(password)) {
                currentUser.setPassword(bCryptPasswordEncoder.encode(password));
            }

            savedUser = userRepository.save(currentUser);
        }
        ResponseBasic responseBasic = savedUser != null ?
                ResponseBasic.builder().result(true).build()
                :
                ResponseBasic.builder().result(false).build();
        return responseBasic;
    }

    public ResponseBasic changeProfile(ProfileRequest profileRequest, User currentUser) {

        String name = profileRequest.getName();
        String email = profileRequest.getEmail();
        String password = profileRequest.getPassword();
        int removePhoto = profileRequest.getRemovePhoto();

        boolean result = true;
        boolean isNameError = false;
        boolean isEmailError = false;
        boolean isPasswordError = false;

        User savedUser;

        if (StringUtils.isEmpty(name)) {
            isNameError = true;
            result = false;
        }
        if (StringUtils.isEmpty(email) || (userRepository.findByEmail(email) != null && !userRepository.findByEmail(email).equals(currentUser))) {
            isEmailError = true;
            result = false;
        }
        if (!StringUtils.isEmpty(password) && password.length() < 6) {
            isPasswordError = true;
            result = false;
        }

        if (!result) {
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .name(isNameError ? NAME_ERROR : null)
                    .email(isEmailError ? EMAIL_ERROR : null)
                    .password(isPasswordError ? PASSWORD_ERROR : null)
                    .build();
            ResponseBasic responseBasic = ResponseBasic.builder().result(false).errorMessage(errorMessage).build();
            return responseBasic;
        } else {
            currentUser.setName(name);
            currentUser.setEmail(email);

            if (removePhoto == 1) {
                currentUser.setPhoto(null);
            }

            if (!StringUtils.isEmpty(password)) {
                currentUser.setPassword(bCryptPasswordEncoder.encode(password));
            }

            savedUser = userRepository.save(currentUser);
        }

        ResponseBasic responseBasic = savedUser != null ?
                ResponseBasic.builder().result(true).build()
                :
                ResponseBasic.builder().result(false).build();
        return responseBasic;
    }
}
