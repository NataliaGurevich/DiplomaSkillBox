package com.skillbox.diploma.DiplomaSkillBox.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //(exclude = {DataSourceAutoConfiguration.class })
public class DiplomaSkillBoxApplication {
	public static void main(String[] args) {
		SpringApplication.run(DiplomaSkillBoxApplication.class, args);
	}
}
