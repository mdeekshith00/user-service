package com.user_service.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.Collection;
=======
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIdentityInfo;

=======
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
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
<<<<<<< HEAD
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
=======
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
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
<<<<<<< HEAD
//@JsonIdentityInfo()
=======
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
public class Users  implements UserDetails , Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2272601944381074300L;
	
	@Id
<<<<<<< HEAD
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer uId;
=======
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
	@Column(nullable = false)
	@Embedded
	private FullName fullName;
	@Column(nullable = false , unique = true)
	private String username;
	@Column(nullable = false , unique = true)
	private String password;
	@Column(nullable = false , unique = true)
	private String phoneNumber;
<<<<<<< HEAD
	
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
	
=======
	@Column
    private Boolean isPhoneNumberVerified;
    @Column(nullable = true)
	private String gender;
    @Column
	private String eMail;
	@Column(name = "address_type" , nullable = false)
	private String addressType;
	@Embedded
	private Address address ;
    @Past
	private LocalDate dateOfBirth;
    @Column
	private Boolean isActive;
	@Column
	private String activeStatus; // e.g., "ACTIVE", "INACTIVE", "BANNED", "PENDING_APPROVAL"
	@Column
	private Long loginCount;
	@Column
	private Timestamp lastLogin;
	@Column
	private LocalDateTime createdAt;
	@Column
	private LocalDateTime updatedAt;
	@Column
	private String resetToken;
	@Column
	private String bio;
	@Column
	private String logInProvider;
	
	@Column(name = "want_to_donate")
	private Boolean wantToDonate; 
	
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
	@PrePersist
	protected void onCreate() {
	    createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
	    updatedAt = LocalDateTime.now();
	}

	
<<<<<<< HEAD
	@ManyToMany(fetch = FetchType.EAGER)
=======
	@ManyToMany(fetch = FetchType.LAZY)
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
	@JoinTable(name = "users_role", 
			joinColumns  = @JoinColumn(name = "user_id") ,
	        inverseJoinColumns = @JoinColumn(name = "role_Id")
	)
<<<<<<< HEAD
=======
	@JsonIgnore
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
	private Set<Role> roles;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return roles.stream()
				.map(role -> (GrantedAuthority)() -> role.getRole())
				.toList();
				
	}
<<<<<<< HEAD

	

}
=======
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
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
