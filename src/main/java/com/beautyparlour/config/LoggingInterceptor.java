package com.beautyparlour.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Interceptor for logging HTTP requests and responses
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String REQUEST_START_TIME = "requestStartTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(REQUEST_START_TIME, startTime);
        
        if (logger.isDebugEnabled()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            logger.debug("=== INCOMING REQUEST ===");
            logger.debug("Timestamp: {}", timestamp);
            logger.debug("Method: {}", request.getMethod());
            logger.debug("URL: {}", request.getRequestURL().toString());
            logger.debug("Query String: {}", request.getQueryString());
            logger.debug("Client IP: {}", clientIp);
            logger.debug("User Agent: {}", userAgent);
            logger.debug("Content Type: {}", request.getContentType());
            
            // Log headers (excluding sensitive ones)
            logger.debug("Headers:");
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                if (!isSensitiveHeader(headerName)) {
                    logger.debug("  {}: {}", headerName, request.getHeader(headerName));
                }
            });
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) {
        if (logger.isDebugEnabled()) {
            Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                
                logger.debug("=== RESPONSE ===");
                logger.debug("Status: {}", response.getStatus());
                logger.debug("Content Type: {}", response.getContentType());
                logger.debug("Duration: {} ms", duration);
                
                if (ex != null) {
                    logger.error("Exception occurred: {}", ex.getMessage(), ex);
                }
                
                logger.debug("========================");
            }
        }
        
        // Log performance warning for slow requests
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 1000) { // Log if request takes more than 1 second
                logger.warn("Slow request detected: {} {} took {} ms", 
                           request.getMethod(), request.getRequestURI(), duration);
            }
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private boolean isSensitiveHeader(String headerName) {
        String lowerCaseHeader = headerName.toLowerCase();
        return lowerCaseHeader.contains("authorization") || 
               lowerCaseHeader.contains("cookie") || 
               lowerCaseHeader.contains("password") ||
               lowerCaseHeader.contains("token");
    }
}
