package com.user_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.common.vo.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

	    private final KafkaTemplate<String, String> kafkaTemplate;
	    private final ObjectMapper objectMapper = new ObjectMapper();
	    private static final String TOPIC = "user-events";
	    
	    public void send(UserEvent event) {
	        try {
	            String json = objectMapper.writeValueAsString(event);
	            kafkaTemplate.send(TOPIC, json);
	            System.out.println("Produced: " + json);
	        } catch (JsonProcessingException e) {
	            throw new RuntimeException("Error converting event to JSON");
	        }
	    }
	      
}
