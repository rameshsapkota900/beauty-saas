package com.example.beautysaas.service;

import com.example.beautysaas.dto.category.CategoryStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryStatsCacheService {
    
    private final CategoryService categoryService;
    
    @Cacheable(value = "categoryStats", key = "#parlourId")
    public CategoryStatsDto getCachedCategoryStats(String adminEmail, UUID parlourId) {
        log.debug("Cache miss for category stats of parlour: {}", parlourId);
        return categoryService.getCategoryStatistics(adminEmail, parlourId);
    }
    
    @CacheEvict(value = "categoryStats", key = "#parlourId")
    public void evictCacheForParlour(UUID parlourId) {
        log.debug("Evicting category stats cache for parlour: {}", parlourId);
    }
    
    @CacheEvict(value = "categoryStats", allEntries = true)
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void evictAllCachesAtInterval() {
        log.debug("Scheduled eviction of all category stats caches");
    }
    
    @CacheEvict(value = "categoryStats", allEntries = true)
    public void evictAllCaches() {
        log.debug("Evicting all category stats caches");
    }
}
