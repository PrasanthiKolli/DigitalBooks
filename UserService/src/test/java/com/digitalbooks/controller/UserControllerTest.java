package com.digitalbooks.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import com.digitalbooks.UserServiceApplication;
import com.digitalbooks.repository.SubscriptionRepository;
import com.digitalbooks.repository.UserRepository;
import com.digitalbooks.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = { UserServiceApplication.class })
@WebAppConfiguration
class UserControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@SpyBean
	UserService userService;

	@MockBean
	UserRepository userRepository;
	
	@MockBean
	SubscriptionRepository subscriptionRepository;
		
	private MockMvc mockMvc;	

	
	@BeforeEach
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}
	
	@Test
	void testRegisterUser() throws Exception {
		
		when(userRepository.save(any())).thenReturn(null);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\""
					   		+ "}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isOk())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("User registered successfully!"));
	}

	@Test
	void testRegisterUserAsReader() throws Exception {
		when(userRepository.save(any())).thenReturn(null);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\",\r\n"
					   		+ "    \"role\": [\r\n"
					   		+ "        \"reader\"\r\n"
					   		+ "    ]}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isOk())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("User registered successfully!"));
	}
	
	@Test
	void testRegisterUserAsAuthor() throws Exception {
		when(userRepository.save(any())).thenReturn(null);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\",\r\n"
					   		+ "    \"role\": [\r\n"
					   		+ "        \"author\"\r\n"
					   		+ "    ]}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isOk())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("User registered successfully!"));
	}
	
	@Test
	void testRegisterUserForExistingUserName() throws Exception {
		
		when(userRepository.existsByUserName(any())).thenReturn(true);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\""
					   		+ "}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isBadRequest())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("Error: UserName is already taken!"));
	}
	
	@Test
	void testRegisterUserForExistingEmailId() throws Exception {
		
		when(userRepository.existsByemailId(any())).thenReturn(true);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\""
					   		+ "}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isBadRequest())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("Error: EmailId is already in use!"));
	}
	
	@Test
	void testRegisterUserForExistingPhoneNumber() throws Exception {
		
		when(userRepository.existsByPhoneNumber(any())).thenReturn(true);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\""
					   		+ "}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isBadRequest())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("Error: PhoneNumber is already in use!"));
	}
}