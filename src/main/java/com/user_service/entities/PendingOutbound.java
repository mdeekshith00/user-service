package com.user_service.entities;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import com.user_service.mapper.MapToJsonConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pending_outbound")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingOutbound implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pending_outbound_id")
    private Integer pendingOutboundId;

    @Column(name="target_url", nullable=false)
    private String targetUrl;

    // store everything as plain string
    @Column(name = "payload", nullable = false, columnDefinition = "text")
    private String payload;

    private int attempts;
    
    private String lastError;

    @Column(name="created_at")
    private OffsetDateTime createdAt;
    
    private String header;
}
