package com.amrsamy.dispatchgrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DispatchGridApplication {

    public static void main(String[] args) {
        SpringApplication.run(DispatchGridApplication.class, args);
    }
}
