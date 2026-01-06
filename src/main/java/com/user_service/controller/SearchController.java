package com.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.common.dto.SearchResultDTO;
import com.user_service.service.SearchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController("/user")
@RequiredArgsConstructor
public class SearchController {
	
	private final SearchService searchService;
	
	@SuppressWarnings("unchecked")
	@GetMapping("/search")
	public ResponseEntity<SearchResultDTO> search(@RequestParam String bloodGroup, @RequestParam(required = false) String location
			, @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {
		return (ResponseEntity<SearchResultDTO>) ResponseEntity.ok(searchService.search(bloodGroup, location, page, size)).status(HttpStatus.OK);
	}

}
