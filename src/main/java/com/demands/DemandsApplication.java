package com.demands;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class DemandsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemandsApplication.class, args);
	}
}