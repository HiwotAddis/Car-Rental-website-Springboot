package com.example.carrentalsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {
	 @Bean
	    public BCryptPasswordEncoder passwordEncoder_2() {
	        return new BCryptPasswordEncoder();
	    }

}

