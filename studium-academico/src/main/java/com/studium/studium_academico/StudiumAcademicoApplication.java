package com.studium.studium_academico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
public class StudiumAcademicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudiumAcademicoApplication.class, args);
	}

}
