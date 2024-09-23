package com.task.books.service;

import com.task.books.model.Book;
import com.task.books.model.BookRating;
import com.task.books.model.BookRatingKey;
import com.task.books.model.Client;
import com.task.books.repository.BookRatingRepository;
import com.task.books.repository.BookRepository;
import com.task.books.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookRatingService {

    private final BookRatingRepository bookRatingRepository;
    private final BookRepository bookRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public BookRatingService(BookRepository bookRepository, BookRatingRepository bookRatingRepository, ClientRepository clientRepository) {
        this.bookRepository = bookRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.clientRepository = clientRepository;
    }

    public Book rateBook(Integer bookId, Integer clientId , int rating) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new EntityNotFoundException("Client not found"));

        BookRatingKey bookRatingKey = new BookRatingKey();
        bookRatingKey.setBookId(bookId);
        bookRatingKey.setClientId(clientId);

        BookRating bookRating = bookRatingRepository.findById(bookRatingKey)
                .orElse(new BookRating());

        bookRating.setId(bookRatingKey);
        bookRating.setBook(book);
        bookRating.setClient(client);
        bookRating.setRating(rating);

        bookRatingRepository.save(bookRating);

        double newAverageRating = book.getRatings().stream().mapToDouble(BookRating::getRating).average().orElse(0);

        book.setAvgRating(newAverageRating);

        bookRepository.save(book);

        return book;
    }

}
