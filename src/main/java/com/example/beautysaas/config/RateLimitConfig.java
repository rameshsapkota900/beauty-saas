package com.example.beautysaas.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig implements WebMvcConfigurer {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityRateLimitInterceptor(this::resolveBucket))
            .addPathPatterns("/api/v1/security/challenges/**");
    }

    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, this::newBucket);
    }

    private Bucket newBucket(String key) {
        return Bucket4j.builder()
            .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1))))
            .addLimit(Bandwidth.classic(1000, Refill.intervally(1000, Duration.ofHours(1))))
            .build();
    }
}
