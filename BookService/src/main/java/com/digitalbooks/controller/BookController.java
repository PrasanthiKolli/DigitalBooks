package com.digitalbooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbooks.model.Book;
import com.digitalbooks.response.MessageResponse;
import com.digitalbooks.service.BookService;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/digitalbooks")
public class BookController {
	
	@Autowired
	BookService bookService;

	@PostMapping("/author/{author-id}/createBook")
	public ResponseEntity<?> createBook(@RequestBody Book book, @PathVariable("author-id") Long id){
		book.setAuthorId(id);
		return bookService.createBook(book, id);
	}
	
	/*
	 * Author can block/unblock his book
	 */
	@GetMapping("/author/{authorId}/blockBook/{bookId}")
	public ResponseEntity<?> blockBook(@PathVariable("authorId") Long authorId, @PathVariable("bookId") Long bookId, @RequestParam("block") boolean block) {
		if (bookService.blockBook(authorId, bookId, block)) return ResponseEntity.ok().body(new MessageResponse("Book updated successfully"));
		return ResponseEntity.internalServerError().body(new  MessageResponse("Book updation failed"));
	}
	
	/*
	 * Author can update his book
	 */
	@PostMapping("/author/{author-id}/updateBook/{book-id}")
	public MessageResponse updateBook(@RequestBody Book book, @PathVariable("author-id") Long authorId, @PathVariable("book-id") Long bookId) {
		if(bookService.updateBook(book, bookId, authorId)) {
			return new MessageResponse("Book updated Successfully");
		}
		return new MessageResponse("Book updation failed");
	}
	
	/*
	 * search books
	 */
	@GetMapping("/book/searchBooks")
	public List<Book> readBook(@RequestParam("category") String category, @RequestParam("title") String title,
			@RequestParam("author") String author, @RequestParam("price") float price,  @RequestParam("publisher") String publisher) {
		return bookService.searchBooks(category, title, author, price, publisher);
	}
	
	/*
	 * get book
	 */
	@GetMapping("/getBook/{book-id}")
	public Book getBook(@PathVariable("book-id") Long bookId) {
		return bookService.getBook(bookId);
	}
	
	/*
	 * get all subscribed books of user 
	 */
	@PostMapping("/book/getSubscribedBooks")
	public ResponseEntity<?> getAllSubscribedBooks(@RequestBody List<Long> bookIds){
		if(bookIds.isEmpty())
			return ResponseEntity.badRequest().body("Invalid books");
		List<Book> book = bookService.getAllSubscribedBooks(bookIds);
		if(book.isEmpty())
			return ResponseEntity.badRequest().body(new MessageResponse("User not subscribed to any book"));
		return ResponseEntity.ok(book);
	}
	
	/*
	 * Reader can read his subscribed book
	 */
	@GetMapping("/book/{book-id}/readBook")
	public MessageResponse readBook(@PathVariable("book-id") Long bookId) {
		
		if(bookId == null)
			return new MessageResponse("Invalid book id");
		return bookService.readBook(bookId);
	}
	/*
	 * get all auhtor books
	 */
	@GetMapping("/author/{author-id}/getAuthorBooks")
	public List<Book> getAllAuthorBooks(@PathVariable("author-id") Long authorId) {
		
		return bookService.getAllAuthorBooks(authorId);
	}


}
