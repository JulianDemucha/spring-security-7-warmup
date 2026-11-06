package com.sigmaproject1.project1;

import com.sigmaproject1.project1.config.properties.JwtProperties;
import com.sigmaproject1.project1.config.properties.XsrfProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, XsrfProperties.class})
public class Project1Application {

	public static void main(String[] args) {
		SpringApplication.run(Project1Application.class, args);
	}

}
