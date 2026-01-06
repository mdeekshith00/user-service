package com.user_service.util;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.common.vo.UserEvent;
import com.user_service.service.KafkaProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHelpers {
	
	private final KafkaProducer kafkaProducer;
	
	public void publishUserEvent(Integer userId, String email, String eventType) {
	    UserEvent event = new UserEvent();
	    event.setEventType(eventType);
	    event.setUserId(String.valueOf(userId));
	    event.setEmail(email);
	    event.setTimestamp(Instant.now().toString());

	    try {
	        kafkaProducer.send(event); // assume send is async and handles serialization
	        log.info("Published kafka event {} for userId {}", eventType, userId);
	    } catch (Exception e) {
	        log.error("Failed to publish kafka event {} for userId {}: {}", eventType, userId, e.getMessage());
	    }
	}

	public void publishUserEventWithPayload(Integer userId, String email, String eventType, String payload) {
	    UserEvent event = new UserEvent();
	    event.setEventType(eventType);
	    event.setUserId(String.valueOf(userId));
	    event.setEmail(email);
	    event.setTimestamp(Instant.now().toString());
	    event.setPayload(payload);

	    try {
	        kafkaProducer.send(event);
	        log.info("Published kafka failure event {} for userId {}", eventType, userId);
	    } catch (Exception e) {
	        log.error("Failed to publish kafka failure event {} for userId {}: {}", eventType, userId, e.getMessage());
	    }
	}


}
