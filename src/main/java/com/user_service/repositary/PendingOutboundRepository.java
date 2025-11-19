package com.user_service.repositary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.entities.PendingOutbound;

public interface PendingOutboundRepository extends JpaRepository<PendingOutbound, Integer>{

	List<PendingOutbound> findTop100ByOrderByCreatedAtAsc();

}
