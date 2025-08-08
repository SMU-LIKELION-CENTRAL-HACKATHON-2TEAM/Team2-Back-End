package org.example.team2backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Team2BackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(Team2BackEndApplication.class, args);
    }

}
