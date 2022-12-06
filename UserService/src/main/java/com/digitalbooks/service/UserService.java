package com.digitalbooks.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import com.digitalbooks.jwt.JwtUtils;
import com.digitalbooks.jwt.services.UserDetailsImpl;
import com.digitalbooks.model.Book;
import com.digitalbooks.model.ERoles;
import com.digitalbooks.model.Role;
import com.digitalbooks.model.Subscription;
import com.digitalbooks.model.User;
import com.digitalbooks.payload.request.LoginRequest;
import com.digitalbooks.payload.request.SignUpRequest;
import com.digitalbooks.payload.response.JwtResponse;
import com.digitalbooks.payload.response.MessageResponse;
import com.digitalbooks.repository.RoleRepository;
import com.digitalbooks.repository.SubscriptionRepository;
import com.digitalbooks.repository.UserRepository;

@Service
public class UserService {

	private static final String BOOK_SERVICE_URL = "http://localhost:8092/api/v1/digitalbooks";

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {

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

	public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {

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

	public ResponseEntity<?> createABook(HttpServletRequest request, Book book, Long id) {
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

		MessageResponse result = restTemplate.postForObject(uri, book, MessageResponse.class);
		return ResponseEntity.ok(result);
	}

	public ResponseEntity<?> blockABook(int authorId, int bookId, boolean block) {
		if (ObjectUtils.isEmpty(authorId))
			return ResponseEntity.badRequest().body(new MessageResponse("Author id is not valid"));
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse("Book id is not valid"));

		String uri = BOOK_SERVICE_URL + "/author/" + authorId + "/blockBook/" + bookId + "?block=" + block;

		RestTemplate restTemplate = new RestTemplate();

		MessageResponse result = restTemplate.getForObject(uri, MessageResponse.class);
		return getResultResponseEntity(result);
	}

	private ResponseEntity<?> getResultResponseEntity(MessageResponse result) {
		if (result.getMessage().equals("Book updation failed"))
			return ResponseEntity.badRequest().body(result);
		return ResponseEntity.ok(result);
	}

	public ResponseEntity<?> updateABook(Book book, Long authorId, Long bookId) {
		if (ObjectUtils.isEmpty(authorId))
			return ResponseEntity.badRequest().body(new MessageResponse("Author id is not valid"));
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse("Book id is not valid"));

		String uri = BOOK_SERVICE_URL + "/author/" + authorId + "/updateBook/" + bookId;

		RestTemplate restTemplate = new RestTemplate();

		MessageResponse result = restTemplate.postForObject(uri, book, MessageResponse.class);
		return getResultResponseEntity(result);
	}

	public ResponseEntity<?> searchBooks(String category, String title, String author, int price, String publisher) {
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

		String uri = BOOK_SERVICE_URL + "/book/searchBooks?category=" + category + "&title=" + title + "&author="
				+ author + "&price=" + price + "&publisher=" + publisher;

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> result = restTemplate.getForEntity(uri, List.class);
		List<Book> books = (List<Book>) result.getBody();
		if (books.isEmpty())
			return ResponseEntity.badRequest().body(new MessageResponse("Invalid request"));
		return ResponseEntity.ok(books);

	}

	public ResponseEntity<?> subscribeABook(Subscription subscription, long bookId) {
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse("bookId is mandatory"));
		RestTemplate restTemplate = new RestTemplate();
		String uri = BOOK_SERVICE_URL+"/getBook/"+bookId;
		Book book=restTemplate.getForObject(uri, Book.class);
		if(ObjectUtils.isEmpty(book) && book.getActive()== false){
			return ResponseEntity.badRequest().body(new MessageResponse("bookId is not valid"));
		}
		subscription.setBookId(bookId);
		Optional<User> isUserAvailable = userRepository.findById(subscription.getUserId());
		if (isUserAvailable.isPresent()) {
			User user = isUserAvailable.get();
			Set<Subscription> subscriptions = user.getSubscriptions();
			subscriptions.add(subscription);
			subscriptionRepository.save(subscription);
			return ResponseEntity.ok(userRepository.save(user));
		}
		return ResponseEntity.badRequest().body(new MessageResponse("user not found"));
	}

	public ResponseEntity<?> cancelSubscription(Long userId, Long subscriptionId) {
		if (userId == null)
			return ResponseEntity.badRequest().body(new MessageResponse("user id is mandatory"));
		if (subscriptionId == null)
			return ResponseEntity.badRequest().body(new MessageResponse("subscription id is mandatory"));
		Optional<User> user=userRepository.findById(userId);
		if(!user.isPresent()) {
			return ResponseEntity.badRequest().body(new MessageResponse("Invalid user"));
		}
		Subscription subscription = getSubscription(userId, subscriptionId);
		if (subscription != null) {
			int HOURS_24 = 24 * 60 * 60 * 1000;
			if (System.currentTimeMillis() - subscription.getSubscriptionTime().getTime() > HOURS_24)
				return ResponseEntity.badRequest().body(new MessageResponse("Invalid request"));

			subscription.setActive(false);
			subscriptionRepository.save(subscription);
			return ResponseEntity.ok(new MessageResponse("Cancelled to subscription successfully!"));
		}
		return ResponseEntity.badRequest().body(new MessageResponse("Invalid Subscription"));
	}

	public Subscription getSubscription(Long userId, Long subscriptionId) {

		Subscription subscription = getSubscriptions(userId).stream().filter(sub -> sub.getId().equals(subscriptionId))
				.findAny().orElse(null);
		if (subscription != null) {
			return subscription;
		}

		return null;
	}

	public Set<Subscription> getSubscriptions(Long userId) {

		Set<Subscription> subscriptionsList = new HashSet<>();
		Optional<User> isUserAvailable = userRepository.findById(userId);
		User user = null;
		if (isUserAvailable.isPresent()) {
			user = isUserAvailable.get();
		}
		if (user != null)
			subscriptionsList = user.getSubscriptions();

		return subscriptionsList;
	}
	
	
	public ResponseEntity<?> fetchAllSubscribedBooks(Long userId) {
		if (ObjectUtils.isEmpty(userId) || !userRepository.existsById(userId))
			return ResponseEntity.badRequest().body(new MessageResponse("userId is not valid"));
		
		Set<Subscription> subscriptionsList = getSubscriptions(userId);
		if(!subscriptionsList.isEmpty()) {
			
			List<Long> bookIds = subscriptionsList.stream().map(sub -> sub.getBookId()).collect(Collectors.toList());
			
			String uri = BOOK_SERVICE_URL + "/book/getSubscribedBooks";
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<?> result = restTemplate.postForEntity(uri, bookIds, List.class);
			return ResponseEntity.ok(result.getBody());
		}
		
		return ResponseEntity.badRequest().body(new MessageResponse("invalid request"));
	}
}
