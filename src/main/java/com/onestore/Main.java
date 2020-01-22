package com.onestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableAsync
@ComponentScan("com.onestore.sample")
public class Main {

	public static void main(String[] args) {
		System.out.println("Activate System ..");
		SpringApplication.run(Main.class, args);
	}

}
