package com.theanswr.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages= {"com.theanswr"})
@EnableScheduling
public class BootApplication {
	
	public static void main(String[] args) {

		SpringApplication.run(BootApplication.class, args);
		
	}
}
