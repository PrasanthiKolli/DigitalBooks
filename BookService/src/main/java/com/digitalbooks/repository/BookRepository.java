package com.digitalbooks.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digitalbooks.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

	Boolean existsByAuthorIdAndTitle(Long authorId, String title);

	Boolean existsByAuthorIdAndId(Long authorId, Long id);

	@Query("select books from Book books where books.category = :category and books.title= :title and books.authorName=:author and books.price< :price and books.publisher= :publisher")
	List<Book> findBooksByCategoryAndTitleAndAuthorAndPriceAndPublisher(String category, String title, String author,
			float price, String publisher);

	List<Book> findAllByAuthorId(Long authorId);
}
