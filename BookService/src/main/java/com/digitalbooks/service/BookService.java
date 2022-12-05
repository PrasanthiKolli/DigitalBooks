package com.digitalbooks.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitalbooks.model.Book;
import com.digitalbooks.repository.BookRepository;
import com.digitalbooks.response.MessageResponse;

@Service
public class BookService {

	@Autowired
	BookRepository bookRespository;

	public MessageResponse saveBook(Book book, Long authorId) {

		try {
			if (bookRespository.existsByAuthorIdAndTitle(authorId, book.getTitle())) {
				return new MessageResponse("Book with same title exists!");
			}
			bookRespository.save(book);
		} catch (Exception exception) {
			return new MessageResponse("Error: " + exception.getMessage());
		}

		return new MessageResponse("Book added successfully!");
	}

	public Book getBook(Long bookId) {

		Optional<Book> book = bookRespository.findById(bookId);
		if (book.isPresent())
			return book.get();
		return null;
	}

	public boolean blockBook(Long authorId, Long bookId, boolean block) {
		if (bookRespository.existsByAuthorIdAndId(authorId, bookId)) {
			Book book = getBook(bookId);
			if (book.getActive() == block)
				return false;

			book.setActive(block);
			book = bookRespository.save(book);
			if (book.getActive() == block) {
				return true;
			} else
				return false;
		} else
			return false;

	}

	public boolean updateBook(Book book, Long bookId, Long authorId) {
		if(bookRespository.existsByAuthorIdAndId(authorId, bookId)) {
			Book existedBook = getBook(bookId);
			existedBook.setCategory(book.getCategory());
			existedBook.setContent(book.getContent());
			existedBook.setPrice(book.getPrice());
			existedBook.setPublishedDate(book.getPublishedDate());
			existedBook.setPublisher(book.getPublisher());
			existedBook.setTitle(book.getTitle());
			existedBook.setLogo(book.getLogo());
			
			bookRespository.save(existedBook);
			return true;
		}
		return false;
	}
	
	public List<Book> searchBooks(String category, String title, String author, Long price, String publisher) {
		List<Book> books = bookRespository.findBooksByCategoryAndTitleAndAuthorAndPriceAndPublisher(category, title, author, price, publisher);
		System.out.println(books);
		return books;
	}

}
