package com.example.beauty_saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Enable JPA Auditing for @CreatedDate, @LastModifiedDate
public class BeautySaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeautySaasApplication.class, args);
    }

}
