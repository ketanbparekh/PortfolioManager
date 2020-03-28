package com.fintech.advisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages= {"com.fintech"})
public class AdvisorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdvisorApplication.class, args);
	}

}
