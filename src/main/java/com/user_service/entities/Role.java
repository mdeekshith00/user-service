package com.user_service.entities;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@Entity
@Builder
@Table(name = "roles")
public class Role  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2088123644343682146L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer rId;
	@Column(nullable = false)
	private String role;
	
	private String description; // @Column(nullable = false, unique = true)
	
	@ManyToMany
	private Set<Users> users;

}
