package com.task.books.repository;

import com.task.books.model.BookRating;
import com.task.books.model.BookRatingKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRatingRepository extends JpaRepository<BookRating, BookRatingKey> {

}
