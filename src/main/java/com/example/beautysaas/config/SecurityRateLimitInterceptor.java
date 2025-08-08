package com.example.beautysaas.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.function.Function;

public class SecurityRateLimitInterceptor implements HandlerInterceptor {
    
    private final Function<String, Bucket> bucketResolver;

    public SecurityRateLimitInterceptor(Function<String, Bucket> bucketResolver) {
        this.bucketResolver = bucketResolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = resolveKey(request);
        Bucket bucket = bucketResolver.apply(key);

        if (bucket.tryConsume(1)) {
            return true;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Rate limit exceeded\",\"retryAfter\":\"60\"}");
        return false;
    }

    private String resolveKey(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip + ":" + request.getRequestURI();
    }
}
