package com.maykealisson.thanos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThanosApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThanosApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ThanosApplication.class, args);
	}


	@Override
	public void run(String... args) {
		LOGGER.info("Active run");
	}
}
