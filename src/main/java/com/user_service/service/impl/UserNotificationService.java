package com.user_service.service.impl;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.common.vo.MinDonorVo;
import com.user_service.entities.PendingOutbound;
import com.user_service.entities.Role;
import com.user_service.repositary.PendingOutboundRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationService {

	 private final WebClient webClient;
	 private final PendingOutboundRepository outboundRepo;
//	    @Value("${internal.donor.url}")
	    private  final String donorUrl = "http://localhost:8081/donor/internal/api/donors";
	    
//	    @Value("${internal.service-token}")
	    private final  String serviceToken = "my-shared-secret";


	    public void notifyDonorServiceAsync(Integer userId, String email, Role role) {
	    	
	     MinDonorVo payload = new MinDonorVo(userId, role.getRole().toString(), email); // use proper DTO if you have
          log.debug("userId : " + userId);
          log.debug("email : " + email);
          log.debug("role : " + role.getRole().toString());          

	    	    webClient.post()
                .uri(donorUrl)
                .bodyValue(payload)
                .retrieve()
                .onStatus(status -> status.value() == 401,
                        response -> Mono.error(new RuntimeException("Unauthorized: service token rejected")))
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .onErrorResume(err -> {
                    System.out.println(">>> WebClient error: " + err.getMessage());

                    PendingOutbound po = PendingOutbound.builder()
                            .targetUrl(donorUrl)
                            .payload(payload.toString())
                            .attempts(1)
                            .createdAt(OffsetDateTime.now())
                            .lastError(err.getMessage())
                            .build();
                    outboundRepo.save(po);

                    return Mono.empty();
                })
                .subscribe(); 

	    }
}
