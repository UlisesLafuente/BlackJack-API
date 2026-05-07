package com.Ulises.BlackJackAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BlackJackAPI Application Entry Point.
 * Initializes the Spring Boot application context and starts the embedded server.
 *
 * @author Ulises Lafuente
 */
@SpringBootApplication
public class BlackJackApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlackJackApiApplication.class, args);
	}

}
