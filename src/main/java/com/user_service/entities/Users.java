package com.user_service.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
//@JsonIdentityInfo()
public class Users  implements UserDetails , Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2272601944381074300L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer uId;
	@Column(nullable = false)
	@Embedded
	private FullName fullName;
	@Column(nullable = false , unique = true)
	private String username;
	@Column(nullable = false , unique = true)
	private String password;
	@Column(nullable = false , unique = true)
	private String phoneNumber;
	
    private Boolean isPhoneNumberVerified;

    private String bloodGroup;
    
	private String gender;

	private String eMail;
	@Embedded
	private Address address ;

	private Boolean isAvailableToDonate;
	
	private LocalDate dateOfBirth;
	
	private Boolean isActive;
	
	private Long loginCount;
	
	private Timestamp lastLogin;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	private LocalDateTime lastDonationDate;
	
	private String resetToken;
	
	private String bio;
	
	private String logInProvider;
	
	@PrePersist
	protected void onCreate() {
	    createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
	    updatedAt = LocalDateTime.now();
	}

	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_role", 
			joinColumns  = @JoinColumn(name = "user_id") ,
	        inverseJoinColumns = @JoinColumn(name = "role_Id")
	)
	private Set<Role> roles;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return roles.stream()
				.map(role -> (GrantedAuthority)() -> role.getRole())
				.toList();
				
	}

	

}
