package com.icandoit.boottalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BoottalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoottalkApplication.class, args);
	}

}
