package com.dailywork.aicustomersupport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
//@EntityScan("com.dailywork")
public class AiCustomerSupportApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiCustomerSupportApplication.class, args);
	}

}
