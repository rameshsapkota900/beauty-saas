package com.example.beautysaas.interceptor;

import com.example.beautysaas.service.RateLimitService;
import com.example.beautysaas.util.AuditUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        
        // Get user identifier for rate limiting
        String identifier = getUserIdentifier(request, clientIp);
        
        // Check rate limit based on endpoint type
        boolean rateLimited = false;
        if (endpoint.contains("/login") || endpoint.contains("/register")) {
            rateLimited = rateLimitService.isLoginRateLimited(identifier);
            if (!rateLimited) {
                rateLimitService.recordLoginAttempt(identifier);
            }
        } else {
            rateLimited = rateLimitService.isApiRateLimited(identifier);
            if (!rateLimited) {
                rateLimitService.recordApiCall(identifier);
            }
        }
        
        // Log API access
        AuditUtils.logApiAccess(identifier, endpoint, method, clientIp, userAgent);
        
        if (rateLimited) {
            log.warn("Rate limit exceeded for identifier: {} on endpoint: {}", identifier, endpoint);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
            response.setContentType("application/json");
            return false;
        }
        
        // Add rate limit headers
        int remaining = rateLimitService.getRemainingRequests(identifier);
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 60000)); // 1 minute
        
        return true;
    }

    private String getUserIdentifier(HttpServletRequest request, String clientIp) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            return principal.getName();
        }
        return clientIp;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
