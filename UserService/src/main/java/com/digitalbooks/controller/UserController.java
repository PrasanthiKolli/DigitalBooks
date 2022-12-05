package com.digitalbooks.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.digitalbooks.jwt.JwtUtils;
import com.digitalbooks.jwt.services.UserDetailsImpl;
import com.digitalbooks.model.Book;
import com.digitalbooks.model.ERoles;
import com.digitalbooks.model.Role;
import com.digitalbooks.model.User;
import com.digitalbooks.payload.request.LoginRequest;
import com.digitalbooks.payload.request.SignUpRequest;
import com.digitalbooks.payload.response.JwtResponse;
import com.digitalbooks.payload.response.MessageResponse;
import com.digitalbooks.repository.RoleRepository;
import com.digitalbooks.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/digitalbooks")
public class UserController {
	
	private static final String BOOK_SERVICE_URL= "http://localhost:8092/api/v1/digitalbooks";
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/sign-up")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

		if (userRepository.existsByUserName(signUpRequest.getUserName())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: UserName is already taken!"));
		}
		if (userRepository.existsByemailId(signUpRequest.getEmailId())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: EmailId is already in use!"));
		}

		if (userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: PhoneNumber is already in use!"));
		}
		User user = new User(signUpRequest.getUserName(), encoder.encode(signUpRequest.getPassword()),
				signUpRequest.getEmailId(), signUpRequest.getPhoneNumber());
		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByRole(ERoles.ROLE_READER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "ROLE_AUTHOR":
					Role authorRole = roleRepository.findByRole(ERoles.ROLE_AUTHOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(authorRole);

					break;
				default:
					Role userRole = roleRepository.findByRole(ERoles.ROLE_READER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

	}

	/*
	 * Guest can sign-in using valid credentials
	 */
	@PostMapping("/sign-in")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}
	
	@PostMapping("/author/{author-id}/books")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> createABook(HttpServletRequest request, @Valid @RequestBody Book book, @PathVariable("author-id") Long id) {
		if (ObjectUtils.isEmpty(id))
			return ResponseEntity.badRequest().body(new MessageResponse("Author id is not valid"));
		
		String jwt = jwtUtils.parseJwt(request);
		if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
			String authorName = jwtUtils.getUserNameFromJwtToken(jwt);
			book.setAuthorName(authorName);
		} else {
			return ResponseEntity.badRequest().body("Invalid request");
		}
		String uri = BOOK_SERVICE_URL + "/author/" + id + "/createBook";

		RestTemplate restTemplate = new RestTemplate();

		MessageResponse result = restTemplate.postForObject(uri,book, MessageResponse.class);
		return ResponseEntity.ok(result);
	}
	
	/*
	 * Author can block a book created by him
	 */
	@PostMapping("/author/{author-id}/books/{book-id}")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> blockABook(@PathVariable("author-id") int authorId, @PathVariable("book-id") int bookId, @RequestParam("block") boolean block) {
		if (ObjectUtils.isEmpty(authorId))
			return ResponseEntity.badRequest().body(new MessageResponse("Author id is not valid"));
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse("Book id is not valid"));
		
		String uri = BOOK_SERVICE_URL + "/author/" + authorId + "/blockBook/" + bookId +"?block=" + block;

		RestTemplate restTemplate = new RestTemplate();

		MessageResponse result = restTemplate.getForObject(uri, MessageResponse.class);
		return getResultResponseEntity(result);
	}
	
	private ResponseEntity<?> getResultResponseEntity(MessageResponse result) {
		if(result.getMessage().equals("Book updation failed"))
			return ResponseEntity.badRequest().body(result);
		return ResponseEntity.ok(result);
	}
	
	/*
	 * Author can update a book created by him
	 */
	@PutMapping("/author/{author-id}/books/{book-id}")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> updateABook(@RequestBody Book book, @PathVariable("author-id") Long authorId, @PathVariable("book-id") Long bookId) {
		if (ObjectUtils.isEmpty(authorId))
			return ResponseEntity.badRequest().body(new MessageResponse("Author id is not valid"));
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse("Book id is not valid"));
		
		String uri = BOOK_SERVICE_URL + "/author/" + authorId + "/updateBook/" + bookId;

		RestTemplate restTemplate = new RestTemplate();

		MessageResponse result = restTemplate.postForObject(uri,book, MessageResponse.class);
		return getResultResponseEntity(result);
	}
	
	/* 
	 * Search books
	 */
	
	@GetMapping("/search")
	public ResponseEntity<?> searchBooks(@RequestParam("category") String category, @RequestParam("title") String title,
				@RequestParam("author") String author, @RequestParam("price") int price,  @RequestParam("publisher") String publisher) {
		if (ObjectUtils.isEmpty(category))
			return ResponseEntity.badRequest().body(new MessageResponse("category is not valid"));
		if (ObjectUtils.isEmpty(title))
			return ResponseEntity.badRequest().body(new MessageResponse("title is not valid"));
		if (ObjectUtils.isEmpty(author))
			return ResponseEntity.badRequest().body(new MessageResponse("author is not valid"));
		if (ObjectUtils.isEmpty(publisher))
			return ResponseEntity.badRequest().body(new MessageResponse("publisher is not valid"));
		if (price < 0)
			return ResponseEntity.badRequest().body(new MessageResponse("price is not valid"));
		
		String uri = BOOK_SERVICE_URL + "/book/searchBooks?category="+category+"&title="+title+"&author="+author+"&price="+price+"&publisher="+publisher;
			
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> result = restTemplate.getForEntity(uri, List.class);
		List<Book> books = (List<Book>) result.getBody();
		if(books.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("Invalid request"));
		return ResponseEntity.ok(books);
		
	}
}
