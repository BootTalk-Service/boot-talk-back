package com.icandoit.boottalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableJpaAuditing
@SpringBootApplication
public class BoottalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoottalkApplication.class, args);
	}

}
