package com.user_service.service;

import com.common.dto.SearchResultDTO;

public interface SearchService {

	  public SearchResultDTO search(String bloodGroup, String location, int page, int size);
}
