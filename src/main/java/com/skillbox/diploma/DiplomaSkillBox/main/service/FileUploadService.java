package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ErrorMessage;
import com.skillbox.diploma.DiplomaSkillBox.main.response.ResponseBasic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
public class FileUploadService {

    @Value("${com.cloudinary.cloud_name}")
    private String cloudName;

    @Value("${com.cloudinari.url}")
    private String cloudUri;

    @Value("${com.cloudinary.api_key}")
    private String apiKey;

    @Value("${com.cloudinary.api_secret}")
    private String apiSecret;

    private String fileUpload(MultipartFile file) throws IOException {
        Cloudinary cloudinary = new Cloudinary("cloudinary://" + apiKey + ":" + apiSecret + "@" + cloudName);

        Map uploadResponse = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String photoUrl = uploadResponse.get("url").toString();

        return photoUrl;
    }

    public String fileUploadAvatar(MultipartFile file) throws IOException {
        Cloudinary cloudinary = new Cloudinary("cloudinary://" + apiKey + ":" + apiSecret + "@" + cloudName);
        Map uploadResponse = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

//        for (Object key : uploadResponse.keySet()) {
//            log.info("key {} -> {}", key, uploadResponse.get(key));
//        }

        String photoUrl = cloudUri + cloudName + "/c_scale,h_36,w_36/v" +
                uploadResponse.get("version").toString() + "/" + uploadResponse.get("public_id").toString()
                + "." + uploadResponse.get("format").toString();

        return photoUrl;
    }

    public ResponseEntity<?> uploadImage(MultipartFile image) throws IOException {
        String BIG_IMAGE_ERROR = "Размер файла превышает допустимый размер";
        String NULL_IMAGE_ERROR = "Картинка null";

        if (image != null && !image.isEmpty()) {
            if (image.getBytes().length <= (5 * 1024 * 1024)) {
                return new ResponseEntity(fileUpload(image), OK);
            } else {
                ErrorMessage errorMessage = ErrorMessage.builder().image(BIG_IMAGE_ERROR).build();
                ResponseBasic responseBasic = ResponseBasic.builder().result(false).errorMessage(errorMessage).build();
                return new ResponseEntity(responseBasic, BAD_REQUEST);
            }
        }
        ErrorMessage errorMessage = ErrorMessage.builder().image(NULL_IMAGE_ERROR).build();
        ResponseBasic responseBasic = ResponseBasic.builder().result(false).errorMessage(errorMessage).build();
        return new ResponseEntity(responseBasic, BAD_REQUEST);
    }
}