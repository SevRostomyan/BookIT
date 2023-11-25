package com.bookit.bookit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"classpath:cleaning-prices.properties", "classpath:application.properties"})
public class BookItApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookItApplication.class, args);
	}

}
