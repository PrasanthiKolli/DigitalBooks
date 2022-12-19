package com.digitalbooks.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.digitalbooks.model.Book;
import com.digitalbooks.model.Subscription;
import com.digitalbooks.payload.request.LoginRequest;
import com.digitalbooks.payload.request.SignUpRequest;
import com.digitalbooks.payload.request.SubscriptionRequest;
import com.digitalbooks.service.UserService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/digitalbooks")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping(value="/sign-up",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		return userService.registerUser(signUpRequest);

	}

	/*
	 * Guest can sign-in using valid credentials
	 */
	@PostMapping("/sign-in")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		return userService.authenticateUser(loginRequest);
	}
	
	/*
	 * create book
	 */
	@PostMapping("/author/{author-id}/books")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> createABook(HttpServletRequest request, @Valid @RequestBody Book book,
			@PathVariable("author-id") Long id) {
		return userService.createABook(request, book, id);
	}

	/*
	 *block a book 
	 */
	@PostMapping("/author/{author-id}/books/{book-id}")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> blockABook(@PathVariable("author-id") int authorId, @PathVariable("book-id") int bookId,
			@RequestParam("block") boolean block) {
		return userService.blockABook(authorId, bookId, block);
	}

	/*
	 * update a book
	 */
	@PutMapping("/author/{author-id}/books/{book-id}")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> updateABook(@RequestBody Book book, @PathVariable("author-id") Long authorId,
			@PathVariable("book-id") Long bookId) {
		return userService.updateABook(book, authorId, bookId);
	}

	/*
	 * Search books
	 */

	@GetMapping("/search")
	public ResponseEntity<?> searchBooks(@RequestParam("category") String category, @RequestParam("title") String title,
			@RequestParam("author") String author, @RequestParam("price") float price,
			@RequestParam("publisher") String publisher) {
		return userService.searchBooks(category, title, author, price, publisher);

	}

	/*
	 * Reader can subscribe to a book
	 */
	@PostMapping("/{book-id}/subscribe")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> subscribeABook(@RequestBody Subscription subscription,
			@PathVariable("book-id") long bookId) {
		return userService.subscribeABook(subscription, bookId);

	}
	
	/*
	 * Reader can cancel subscription before 24 hrs
	 */
	@PostMapping("/readers/{user-id}/books/{subscription-id}/cancel-subscription")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> cancelSubscription(@PathVariable("user-id") Long userId, @PathVariable("subscription-id") Long subscriptionId) {
		
		return userService.cancelSubscription(userId, subscriptionId);
	}
	
	/*
	 * Reader can fetch all his subscribed books
	 */
	@GetMapping("/readers/{user-id}/books")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> fetchAllSubscribedBooks(@PathVariable("user-id") Long userId) {

		return userService.fetchAllSubscribedBooks(userId);
	}
	
	/*
	 * Reader can fetch a subscribed book
	 */
	@GetMapping("/readers/{user-id}/books/{subscription-id}")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> fetchSubscribedBook(@PathVariable("user-id") Long userId, @PathVariable("subscription-id") Long subscriptionId) {

		return userService.fetchSubscribedBook(userId,subscriptionId);
	}
	
	/*
	 * Reader can a read a subscribed book
	 */
	@GetMapping("/readers/{user-id}/books/{subscription-id}/read")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> readBook(@PathVariable("user-id") Long userId, @PathVariable("subscription-id") Long subscriptionId) {
		return userService.readBok(userId,subscriptionId);
		
	}
	/*
	 * Author can fetch all books created by him
	 */
	@GetMapping("/author/{author-id}/getAllBooks")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> getAuthorBooks(@PathVariable("author-id") Long authorId) {
		return  userService.getAuthorBooks(authorId);
	}
	
}
