package com.user_service.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.common.dto.PageResponse;
import com.common.dto.SearchBloodInventoryDTO;
import com.common.dto.SearchDonorDTO;
import com.common.dto.SearchHospitalInventoryDTO;
import com.common.dto.SearchResultDTO;
import com.user_service.service.SearchService;
import com.user_service.util.CacheKeyUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final WebClient webClient;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DONOR_URL = "http://donor-service/donors/search";
    private static final String BLOODBANK_URL = "http://bloodbank-service/inventory/search";
    private static final String HOSPITAL_URL = "http://hospital-service/hospitals/inventory/search";

    @Override
    public SearchResultDTO search(String bloodGroup, String location, int page, int size) {
    	
        // 🔥 1. Create cache key
        String cacheKey = CacheKeyUtil.searchKey(bloodGroup, location, page, size);

        // 🔥 2. Check Redis if cached
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("Returning cached result for key: {}", cacheKey);
            return (SearchResultDTO) cached;
        }

        log.info("Cache miss → calling microservices");

        Mono<PageResponse<SearchDonorDTO>> donorMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DONOR_URL)
                        .queryParam("bloodGroup", bloodGroup)
                        .queryParam("location", location)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<SearchDonorDTO>>() {})
                .onErrorResume(e -> {
                    log.error("Error calling donor-service: {}", e.getMessage());
                    return Mono.just(new PageResponse<>());
                });

        Mono<PageResponse<SearchBloodInventoryDTO>> bankMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BLOODBANK_URL)
                        .queryParam("bloodGroup", bloodGroup)
                        .queryParam("location", location)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<SearchBloodInventoryDTO>>() {})
                .onErrorResume(e -> {
                    log.error("Error calling bloodbank-service: {}", e.getMessage());
                    return Mono.just(new PageResponse<>());
                });

        Mono<PageResponse<SearchHospitalInventoryDTO>> hospitalMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(HOSPITAL_URL)
                        .queryParam("bloodGroup", bloodGroup)
                        .queryParam("location", location)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<SearchHospitalInventoryDTO>>() {})
                .onErrorResume(e -> {
                    log.error("Error calling hospital-service: {}", e.getMessage());
                    return Mono.just(new PageResponse<>());
                });

        // 4. Combine async results
        SearchResultDTO result = Mono.zip(donorMono, bankMono, hospitalMono)
                .map(tuple -> new SearchResultDTO(
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3()))
                .block();
        // 🔥 5. Store in Redis with TTL 5 minutes
        redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);

        return result;
    }
}
