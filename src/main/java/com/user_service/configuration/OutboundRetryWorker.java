package com.user_service.configuration;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.user_service.entities.PendingOutbound;
import com.user_service.repositary.PendingOutboundRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OutboundRetryWorker {

    private final PendingOutboundRepository repo;
    private final WebClient webClient;
    private final String serviceToken = "my-shared-secret"; // inject via @Value("${internal.service-token}")
    private final int MAX_ATTEMPTS = 5;

    @Scheduled(fixedDelayString = "PT30S")
    public void retryPending() {
        List<PendingOutbound> list = repo.findTop100ByOrderByCreatedAtAsc();
        for (PendingOutbound p : list) {
            webClient.post()
                    .uri(p.getTargetUrl())
                    .header("X-Service-Token", serviceToken)
                    .bodyValue(p.getPayload())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnSuccess(v -> repo.delete(p))
                    .onErrorResume(err -> {
                        p.setAttempts(p.getAttempts() + 1);
                        p.setLastError(err.getMessage());
                        if (p.getAttempts() >= MAX_ATTEMPTS) {
                            repo.delete(p);
                        } else {
                            repo.save(p);
                        }
                        return Mono.empty();
                    })
                    .subscribe();

        }
    }
}
