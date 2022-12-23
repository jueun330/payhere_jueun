package com.example.payhere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PayhereApplication {

	public static void main(String[] args) {

		SpringApplication.run(PayhereApplication.class, args);
		System.out.println("실행");
	}
}
