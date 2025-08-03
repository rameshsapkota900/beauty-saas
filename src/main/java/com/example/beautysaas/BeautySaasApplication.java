package com.example.beautysaas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing // Enables JPA Auditing for createdDate, lastModifiedDate
@EnableAspectJAutoProxy(proxyTargetClass = true) // Needed for @PreAuthorize
@EnableScheduling // For potential future scheduled tasks like notifications
public class BeautySaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeautySaasApplication.class, args);
    }

}
