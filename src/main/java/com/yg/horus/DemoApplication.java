package com.yg.horus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.yg.horus")
public class DemoApplication {

	public static void main(String[] args) {
		System.out.println("Active System..");
		SpringApplication.run(DemoApplication.class, args);
	}
}
