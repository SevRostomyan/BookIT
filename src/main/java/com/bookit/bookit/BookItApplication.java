package com.bookit.bookit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
//@ComponentScan({"com.bookit.bookit.config", "com.bookit.bookit.repository", "com.bookit.bookit.controller", "com.bookit.bookit.service"})
public class BookItApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookItApplication.class, args);
    }



}
