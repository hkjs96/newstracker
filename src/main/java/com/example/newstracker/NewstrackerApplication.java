package com.example.newstracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NewstrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewstrackerApplication.class, args);
	}

}
