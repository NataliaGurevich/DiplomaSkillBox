package com.skillbox.diploma.DiplomaSkillBox.main.service;

import com.skillbox.diploma.DiplomaSkillBox.main.model.GlobalSettings;
import com.skillbox.diploma.DiplomaSkillBox.main.repository.GlobalSettingsRepository;
import com.skillbox.diploma.DiplomaSkillBox.main.request.GlobalSettingsRequest;
import com.skillbox.diploma.DiplomaSkillBox.main.response.GlobalSettingsResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Data
@Service
@Transactional
public class GlobalSettingsService {

    @Autowired
    GlobalSettingsRepository globalSettingsRepository;

    public GlobalSettingsResponse globalSettingsResponse() {

        boolean MULTIUSER_MODE = false;
        boolean POST_PREMODERATION = true;
        boolean STATISTICS_IS_PUBLIC = true;

        List<GlobalSettings> globalSettings = globalSettingsRepository.findAll();

        if (globalSettings != null) {

            for (GlobalSettings settings : globalSettings) {
                switch (settings.getCode()) {
                    case "MULTIUSER_MODE":
                        MULTIUSER_MODE = settings.getValue().toLowerCase().equals("YES".toLowerCase());
                        break;
                    case "POST_PREMODERATION":
                        POST_PREMODERATION = settings.getValue().toLowerCase().equals("YES".toLowerCase());
                        break;
                    case "STATISTICS_IS_PUBLIC":
                        STATISTICS_IS_PUBLIC = settings.getValue().toLowerCase().equals("YES".toLowerCase());
                        break;
                }
            }
        }

        GlobalSettingsResponse globalSettingsResponse = new GlobalSettingsResponse();
        globalSettingsResponse.setMULTIUSER_MODE(MULTIUSER_MODE);
        globalSettingsResponse.setPOST_PREMODERATION(POST_PREMODERATION);
        globalSettingsResponse.setSTATISTICS_IS_PUBLIC(STATISTICS_IS_PUBLIC);

        log.info("IN GLOBALSETTINGS {}", globalSettingsResponse);

        return globalSettingsResponse;
    }

    public void saveGlobalSettings(GlobalSettingsRequest globalSettingsRequest) {

        List<GlobalSettings> globalSettings = globalSettingsRepository.findAll();

        if (globalSettings != null) {

            for (GlobalSettings settings : globalSettings) {
                switch (settings.getCode()) {
                    case "MULTIUSER_MODE":
                        settings.setValue(globalSettingsRequest.isMULTIUSER_MODE() ? "YES" : "NO");
                        globalSettingsRepository.save(settings);
                        break;
                    case "POST_PREMODERATION":
                        settings.setValue(globalSettingsRequest.isPOST_PREMODERATION() ? "YES" : "NO");
                        globalSettingsRepository.save(settings);
                        break;
                    case "STATISTICS_IS_PUBLIC":
                        settings.setValue(globalSettingsRequest.isSTATISTICS_IS_PUBLIC() ? "YES" : "NO");
                        globalSettingsRepository.save(settings);
                        break;
                }
            }
        }
    }
}
