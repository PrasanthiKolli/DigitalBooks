package com.digitalbooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/v1/digitalbooks")
public class BookController {
	
	@Autowired
	BookService bookService;

	@PostMapping("/author/{author-id}/createBook")
	public ResponseEntity<?> createBook(@RequestBody Book book, @PathVariable("author-id") Long id){
		book.setAuthorId(id);
		return ResponseEntity.ok(bookService.saveBook(book, id));
	}
	
	/*
	 * Author can block/unblock his book
	 */
	@GetMapping("/author/{authorId}/blockBook/{bookId}")
	public MessageResponse getSubscribedBook(@PathVariable("authorId") Long authorId, @PathVariable("bookId") Long bookId, @RequestParam("block") boolean block) {
		if (bookService.blockBook(authorId, bookId, block)) return new MessageResponse("Book updated successfully");
		return new MessageResponse("Book updation failed");
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
			@RequestParam("author") String author, @RequestParam("price") Long price,  @RequestParam("publisher") String publisher) {
		return bookService.searchBooks(category, title, author, price, publisher);
	}


}
