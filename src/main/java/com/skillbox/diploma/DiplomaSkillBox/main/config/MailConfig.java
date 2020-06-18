package com.skillbox.diploma.DiplomaSkillBox.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.email.host}")
    private String mailServerHostName;

    @Value("${spring.email.port}")
    private int mailServerPort;

    @Value("${spring.email.username}")
    private String mailServerUsername;

    @Value("${spring.email.password}")
    private String mailServerPassword;

    @Value("${spring.email.protocol}")
    private String mailServerProtocol;

    @Value("${mail.debug}")
    private String debug;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(mailServerHostName);
        javaMailSender.setPort(mailServerPort);
        javaMailSender.setUsername(mailServerUsername);
        javaMailSender.setPassword(mailServerPassword);

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", mailServerProtocol);
        properties.setProperty("mail.debug", debug);

        return javaMailSender;
    }
}
