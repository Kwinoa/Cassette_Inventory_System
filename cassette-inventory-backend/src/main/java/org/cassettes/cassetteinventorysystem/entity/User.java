package org.cassettes.cassetteinventorysystem.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
 
@Entity
@Table(name = "users")
public class User {
     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     
    @Column(nullable = false, unique = true, length = 45)
    private String email;
     
    @Column(nullable = false, length = 64)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
     
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;
     
    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;
    
    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate = LocalDate.now();
    
    @Column(name = "smart_limit", nullable = false)
    private int smartLimit = 10;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Cassette> cassettes = new ArrayList<>();
    
	@ElementCollection
	@CollectionTable(name = "cassette_months", joinColumns = @JoinColumn(name = "user_id"))
	private List<Month> month_list = new ArrayList<>();
    
    @JsonIgnore
    @Column(name="refresh_token", nullable = true, length=200)
    private String spotifyRefreshToken;

	public User(Long id, String email, String password, String firstName, String lastName, List<Month> month_list, int smartLimit, LocalDate joinDate) {
		super();
		this.id = id;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.month_list = month_list;
		this.smartLimit = smartLimit;
		this.joinDate = joinDate;
	}

	public User() {
		super();
	}

	@PrePersist
	private void initializeMonthList() {
		if (month_list == null) {
			month_list = new ArrayList<>();
		}
		if (month_list.isEmpty()) {
			for (int month = 1; month <= 12; month++) {
				month_list.add(new Month(month, 0));
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public LocalDate getJoinDate() {
		return joinDate;
	}
	
	public void setJoinDate(LocalDate joinDate) {
		this.joinDate = joinDate;
	}
	
	public List<Month> getMonth_list(){
		return month_list;
	}
	
	public void setMonth_list(List<Month> month_list){
		this.month_list = month_list;
	}
	
	public int getSmartLimit() {
		return smartLimit;
	}
	
	public void setSmartLimit(int limit) {
		this.smartLimit = limit;
	}
	
	public String getSpotifyRefreshToken() {
		return spotifyRefreshToken;
	}
	
	public void setSpotifyRefreshToken(String refreshToken) {
		this.spotifyRefreshToken = refreshToken;
	}
     
}
