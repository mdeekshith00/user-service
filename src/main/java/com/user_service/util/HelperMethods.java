package com.user_service.util;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.common.dto.CampCoordinatorCreateRequestDto;
import com.common.dto.DonorCreateRequestDto;
import com.common.dto.HospitalAdminCreateRequestDto;
import com.common.dto.HospitalStaffCreateRequestDto;
import com.common.dto.VolunteerCreateRequestDto;
import com.common.enums.PendingOutboundStatus;
import com.user_service.entities.PendingOutbound;
import com.user_service.repositary.PendingOutboundRepository;
import com.user_service.service.impl.JsonUtil;
import com.user_service.vo.UsersVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelperMethods {
	
	private final WebClient webClient;
	private final WebClient donorWebClient;
	private final PendingOutboundRepository outboundRepo;
	
	
	private final String donorUrl = "http://localhost:8081/donor/internal/api/donors";
    private final String hospitalAdminUrl = "http://localhost:8082/hospital/internal/api/hospitals/admins";
    private final String hospitalStaffUrl = "http://localhost:8082/hospital/internal/api/hospitals/staff";
    private final String campCoordinatorUrl = "http://localhost:8083/camp/internal/api/coordinators";
    private final String volunteerUrl = "http://localhost:8083/camp/internal/api/volunteers";
	private final String serviceToken = "my-shared-secret";

	public void notifyDonorService(Integer userId, String email, UsersVo vo) {
	    DonorCreateRequestDto dto = new DonorCreateRequestDto();
	    dto.setUserId(userId);
	    dto.setEmail(email);
	    dto.setFullName(vo.getFullname().getSecondName());
	    dto.setPhoneNumber(vo.getPhoneNumber());
	    dto.setGender(vo.getGender() != null ? vo.getGender().name() : null);
	    dto.setDateOfBirth(vo.getDateOfBirth());
	    dto.setBloodGroup(vo.getBloodGroup().name());
	    
	    String payload = JsonUtil.toJson(dto); // helper to convert to JSON (implement below)
        String url = donorUrl + "?token=" + serviceToken;

	    // Fire and forget REST call, but block short time to ensure success for sync
        try {
            donorWebClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(5))
                    .doOnError(e -> log.warn("donor-service notify failed: {}", e.getMessage()))
                    .onErrorResume(e -> Mono.error(e))
                    .block();

            // success log
            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(null)
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.SUCCESS.name())
                    .build();
            outboundRepo.save(po);
        } catch (Exception ex) {
            log.warn("donor-service REST notify failed for userId {}: {}", userId, ex.getMessage());
            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(ex.getMessage())
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.FAILED.name())
                    .build();
            outboundRepo.save(po);
        }
	}

	public void notifyHospitalServiceForAdmin(Integer userId, String email, UsersVo vo) {
	    HospitalAdminCreateRequestDto dto = new HospitalAdminCreateRequestDto();
	    dto.setUserId(userId);
        dto.setEmail(email);
        dto.setPhoneNumber(vo.getPhoneNumber());

        
        String payload = JsonUtil.toJson(dto);
        String url = hospitalAdminUrl;

        try {
            webClient.post().uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(null)
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.SUCCESS.name())
                    .build();
            outboundRepo.save(po);
        } catch (Exception ex) {
            log.warn("hospital-service admin notify failed for userId {}: {}", userId, ex.getMessage());
            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(ex.getMessage())
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.FAILED.name())
                    .build();
            outboundRepo.save(po);
        }
	}
	public void notifyHospitalServiceForStaff(Integer userId, String email, UsersVo vo) {
	    HospitalStaffCreateRequestDto dto = new HospitalStaffCreateRequestDto();
	    dto.setUserId(userId);
        dto.setEmail(email);
        dto.setPhoneNumber(vo.getPhoneNumber());
//        dto.setHospitalId(vo.getHospitalId());

        String payload = JsonUtil.toJson(dto);
        String url = hospitalStaffUrl;

        try {
            webClient.post().uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(null)
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.SUCCESS.name())
                    .build();
            outboundRepo.save(po);
        } catch (Exception ex) {
            log.warn("hospital-service staff notify failed for userId {}: {}", userId, ex.getMessage());
            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(ex.getMessage())
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.FAILED.name())
                    .build();
            outboundRepo.save(po);
        }
	}

	public void notifyCampServiceCoordinator(Integer userId, String email, UsersVo vo) {
	    CampCoordinatorCreateRequestDto dto = new CampCoordinatorCreateRequestDto();
	    dto.setUserId(userId);
        dto.setEmail(email);
        dto.setPhoneNumber(vo.getPhoneNumber());

        String payload = JsonUtil.toJson(dto);
        String url = campCoordinatorUrl;

        try {
            webClient.post().uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(null)
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.SUCCESS.name())
                    .build();
            outboundRepo.save(po);
        } catch (Exception ex) {
            log.warn("camp-service coordinator notify failed for userId {}: {}", userId, ex.getMessage());
            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(ex.getMessage())
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.FAILED.name())
                    .build();
            outboundRepo.save(po);
        }
	}

	public void notifyCampServiceVolunteer(Integer userId, String email, UsersVo vo) {
	    VolunteerCreateRequestDto dto = new VolunteerCreateRequestDto();
	    dto.setUserId(userId);
        dto.setEmail(email);
        dto.setPhoneNumber(vo.getPhoneNumber());

        String payload = JsonUtil.toJson(dto);
        String url = volunteerUrl;

        try {
            webClient.post().uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(null)
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.SUCCESS.name())
                    .build();
            outboundRepo.save(po);
        } catch (Exception ex) {
            log.warn("camp-service volunteer notify failed for userId {}: {}", userId, ex.getMessage());
            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(url)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(ex.getMessage())
                    .header("token=" + serviceToken)
                    .status(PendingOutboundStatus.FAILED.name())
                    .build();
            outboundRepo.save(po);
        }
	}
        /* Save a very simple outbound record for events or errors (small utility used above) */
     public void saveOutboundLogSimple(Integer userId, String targetLabel, String eventType, String payload) {
            PendingOutbound po = PendingOutbound.builder()
                    .targetUrl(targetLabel)
                    .payload(payload)
                    .attempts(1)
                    .createdAt(OffsetDateTime.now())
                    .lastError(null)
                    .header("userId=" + userId + ",event=" + eventType)
                    .status(PendingOutboundStatus.SUCCESS.name())
                    .build();
            outboundRepo.save(po);
        }

     /* --- JSON util (simple) --- */
     private static class JsonUtil {
         static String toJson(Object o) {
             try {
                 // Use your favourite serializer; using Jackson here:
                 return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(o);
             } catch (Exception e) {
                 return "{}";
             }
         }
     }


}
