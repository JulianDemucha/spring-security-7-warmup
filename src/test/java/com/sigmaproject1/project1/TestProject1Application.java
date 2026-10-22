package com.sigmaproject1.project1;

import org.springframework.boot.SpringApplication;

public class TestProject1Application {

	public static void main(String[] args) {
		SpringApplication.from(Project1Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
