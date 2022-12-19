package com.digitalbooks.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.digitalbooks.model.Book;
import com.digitalbooks.repository.BookRepository;
import com.digitalbooks.response.MessageResponse;

@Service
public class BookService {

	@Autowired
	BookRepository bookRespository;

	public ResponseEntity<?> createBook(Book book, Long authorId) {

		try {
			if (bookRespository.existsByAuthorIdAndTitle(authorId, book.getTitle())) {
				return ResponseEntity.badRequest().body("Book with same title exists");
			}
			book.setPublishedDate(Timestamp.valueOf(LocalDateTime.now()));
			bookRespository.save(book);
		} catch (Exception exception) {
			return ResponseEntity.internalServerError().body("Error: " + exception.getMessage());
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	public Book getBook(Long bookId) {
		Optional<Book> book = bookRespository.findById(bookId);
		if (book.isPresent())
			return book.get();
		return null;
	}
	
	public String checkBook(Long bookId) {
		return bookRespository.existsById(bookId) ? "BookFound" : "INVALID_BOOKID";
	}

	public boolean blockBook(Long authorId, Long bookId, boolean block) {
		if(Boolean.TRUE.equals(bookRespository.existsByAuthorIdAndId(authorId, bookId))) {
			Book book = getBook(bookId);
			if(book.getActive() != block)
				return false;
			book.setActive(!block);

			Book book1 = bookRespository.save(book);
			if(book1.getActive() != block) {
				return true;
			} else 
				return false;
		} else
			return false;
	}

	public boolean updateBook(Book book, Long bookId, Long authorId) {
		if (bookRespository.existsByAuthorIdAndId(authorId, bookId)) {
			Book existedBook = getBook(bookId);
			existedBook.setCategory(book.getCategory());
			existedBook.setContent(book.getContent());
			existedBook.setPrice(book.getPrice());
			existedBook.setPublishedDate(book.getPublishedDate());
			existedBook.setPublisher(book.getPublisher());
			existedBook.setTitle(book.getTitle());
			existedBook.setLogo(book.getLogo());
			existedBook.setActive(book.getActive());

			bookRespository.save(existedBook);
			return true;
		}
		return false;
	}

	public List<Book> searchBooks(String category, String title, String author, float price, String publisher) {
		List<Book> books = bookRespository.findBooksByCategoryAndTitleAndAuthorAndPriceAndPublisher(category, title,
				author, price, publisher);
		System.out.println(books);
		return books;
	}

	public List<Book> getAllSubscribedBooks(List<Long> bookIds) {

		List<Book> booksList = new ArrayList<>();
		List<Book> allSubscribedBooks = bookRespository.findAllById(bookIds);
		booksList = allSubscribedBooks.stream().filter(Book::getActive).collect(Collectors.toList());

		return booksList;

	}

	public MessageResponse readBook(Long bookId) {
		if(bookRespository.existsById(bookId)) {
			Book b= getBook(bookId);
			if(b.getActive()) {
				return new MessageResponse(b.getContent());
			}
		}
		
		return new MessageResponse("invalid Book request");
	}

	public List<Book> getAllAuthorBooks(Long authorId) {

		List<Book> booksList = new ArrayList<>();
		if(ObjectUtils.isEmpty(authorId))
			return booksList;
		
		List<Book> books = new ArrayList<>();
		books = bookRespository.findAllByAuthorId(authorId);
		return books;
	}
}
