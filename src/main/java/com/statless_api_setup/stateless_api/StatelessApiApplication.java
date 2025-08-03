package com.statless_api_setup.stateless_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class StatelessApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(StatelessApiApplication.class, args);
		//for testing purposes
		//creating a BcrptPasswordEncoder
		System.out.println("admin:: "+new BCryptPasswordEncoder().encode("admin"));
		System.out.println("user:: "+new BCryptPasswordEncoder().encode("user"));
	}

}
