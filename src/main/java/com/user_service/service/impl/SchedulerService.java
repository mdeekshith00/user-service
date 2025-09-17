package com.user_service.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class SchedulerService {
	private final UsersServiceImpl userServiceImpl;
	
	@Scheduled(fixedRate = 300000)  // 300000 ms = 5 minutes
	public void runEveryFiveMinutes() {
		log.info("scheduler started for every five minutes :");
		
		
		
	}

}
