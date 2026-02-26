package com.photography.timekeeperbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class TimeKeeperBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeKeeperBackendApplication.class, args);
    }

}
