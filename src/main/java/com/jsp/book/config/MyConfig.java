package com.jsp.book.config;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class MyConfig {

	@Bean
	SecureRandom secureRandom() {
		return new SecureRandom();
	}
}
