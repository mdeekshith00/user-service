package com.user_service.entities;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_log")
public class AuditLog {
	@Id
	@Column(name = "audir_log_id")
	private Integer auditId;
	@Column
	private Integer userId;
	@Column
	private String action; // LOGIN_SUCCESS, PASSWORD_CHANGE, ROLE_UPDATED
	@Column
	private String ipAddress;
	@Column
	private String userAgent;
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();
	 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actor_user_id", nullable = false)
	@JsonBackReference
	private Users user;

}
