package com.worldcup2026.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String MATCHES_CACHE = "matches";
    public static final String STANDINGS_CACHE = "standings";
    public static final String TEAMS_CACHE = "teams";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                MATCHES_CACHE,
                STANDINGS_CACHE,
                TEAMS_CACHE
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .recordStats());

        return cacheManager;
    }

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000);
    }
}
