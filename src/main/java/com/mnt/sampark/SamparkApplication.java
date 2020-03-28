package com.mnt.sampark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SamparkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SamparkApplication.class, args);
	}
}
