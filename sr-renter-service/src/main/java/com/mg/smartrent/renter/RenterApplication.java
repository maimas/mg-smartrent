package com.mg.smartrent.renter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EntityScan(basePackages = {"com.mg"})
@ComponentScan(basePackages = {"com.mg"})
public class RenterApplication {


    public static void main(String[] args) {
        SpringApplication.run(RenterApplication.class, args);
    }


}
