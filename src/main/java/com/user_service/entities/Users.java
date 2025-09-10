package com.user_service.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;
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
public class Users  implements UserDetails , Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2272601944381074300L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
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
    @Column(nullable = true)
	private String gender;

	private String eMail;
	
	@Column(name = "address_type" , nullable = false)
	private String addressType;
	@Embedded
	private Address address ;
    @Past
	private LocalDate dateOfBirth;
    
	private Boolean isActive;
	
	private String activeStatus; // e.g., "ACTIVE", "INACTIVE", "BANNED", "PENDING_APPROVAL"
	
	private Long loginCount;
	
	private Timestamp lastLogin;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	private String resetToken;
	
	private String bio;
	
	private String logInProvider;
	
	@Column(name = "want_to_donate")
	private Boolean wantToDonate; 
	
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
	@OneToOne(mappedBy = "user")
	@JsonManagedReference
    private RefreshToken refreshToken;
	
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	private List<AuditLog> auditLogs = new ArrayList<>();
	
	@OneToMany(mappedBy = "user")
	@JsonManagedReference
	private List<UserHistory> userHistory;
	
	
	
	public UserHistory  addHistory(UserHistory userHistory) {
		getUserHistory().add(userHistory);
		userHistory.setUser(this);
		
		return userHistory;
	}
	
	public UserHistory removeHistory(UserHistory userHistory) {
		getUserHistory().remove(userHistory);
		userHistory.setUser(null);
		
		return userHistory;
	}

}