package com.digitalbooks.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern.Flag;
import javax.validation.constraints.Size;
 
public class SignUpRequest {
    @NotBlank(message = "userName must not be empty")
    @Size(min = 3, max = 20, message = "length must be between 3 to 20 characters")
    private String userName;
 
    @NotBlank(message = "emailId must not be empty")
    @Size(max = 50)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Flag.CASE_INSENSITIVE, message = "please enter valid emailId")
    private String emailId;
    
    private Set<String> role;
    
    @NotBlank(message = "password must not be empty")
    @Size(min = 6, max = 40)
    private String password;
    
    @NotBlank(message = "phoneNumber must not be empty")
    @Size(min = 10, max = 10)
    private String phoneNumber;
     
    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
    
   	public Set<String> getRole() {
		return role;
	}

	public void setRole(Set<String> role) {
		this.role = role;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	

	@Override
	public String toString() {
		return "SignupRequest [userName=" + userName + ", emailId=" + emailId + ", role=" + role + ", password="
				+ password + ", phoneNumber=" + phoneNumber + "]";
	}

}