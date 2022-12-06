package com.digitalbooks.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern.Flag;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Users", 
uniqueConstraints = { 
		@UniqueConstraint(columnNames = "userName"),
		@UniqueConstraint(columnNames = "emailId"),
		@UniqueConstraint(columnNames = "phoneNumber") 
	})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@NotBlank(message = "userName should not be empty")
    @Size(min = 8, max = 15, message = "length should be between 8 to 15 characters")
	String userName;
	
	@NotBlank(message = "password should not be empty ")
    @Size(max = 120)
	String password;
	
	@NotBlank(message = "Enter valid emailId")
    @Size(max = 50)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Flag.CASE_INSENSITIVE, message = "please enter valid emailId")
	String emailId;
	
	@NotBlank(message = "Enter valid Phone number")
    @Size(min = 10, max = 10)
	String phoneNumber;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	Set<Role> roles = new HashSet<>();
	boolean isActive=true;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_subscriptions", 
				joinColumns = @JoinColumn(name = "user_id"),
				inverseJoinColumns = @JoinColumn(name = "subscription_id"))
	Set<Subscription> subscriptions = new HashSet<>();
	public User() {
	}

	public User(String userName, String password, String emailId, String phoneNumber) {
		this.userName = userName;
		this.password = password;
		this.emailId = emailId;
		this.phoneNumber = phoneNumber;
	}
	
	public User(String userName, String password, String emailId, String phoneNumber, Set<Role> roles) {
		this.userName = userName;
		this.password = password;
		this.emailId = emailId;
		this.phoneNumber = phoneNumber;
		this.roles = roles;
	}
	public User(String userName, String password, String emailId, String phoneNumber, Set<Role> roles,Set<Subscription> subscriptions) {
		this.userName = userName;
		this.password = password;
		this.emailId = emailId;
		this.phoneNumber = phoneNumber;
		this.roles = roles;
		this.subscriptions=subscriptions;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public Set<Subscription> getSubscriptions() {
		// TODO Auto-generated method stub
		return subscriptions;
	}
	public void setSubscriptions(Set<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}


	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password=" + password + ", emailId=" + emailId
				+ ", phoneNumber=" + phoneNumber + ", roles=" + roles + ", isActive=" + isActive + "]";
	}
	
	

}