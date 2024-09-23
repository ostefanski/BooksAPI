package com.task.books.service;

import com.task.books.model.*;
import com.task.books.repository.BookRatingRepository;
import com.task.books.repository.BookRepository;
import com.task.books.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookRatingServiceUnitTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookRatingRepository bookRatingRepository;

    @Mock
    private ClientRepository clientRepository;

    private BookRatingService bookRatingService;

    @BeforeEach
    public void setUp() {
        bookRatingService = new BookRatingService(bookRepository, bookRatingRepository, clientRepository);
    }

    @Test
    public void rateBook_CorrectlyRatesBookWithNoExistingRating() {
        Book book = new Book("Test Book", 2022, 0.0);
        book.setBookId(1);
        book.setRatings(new HashSet<>());

        Client client = new Client("Test Client");
        client.setClientId(1);

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(bookRatingRepository.findById(any(BookRatingKey.class))).thenReturn(Optional.empty());

        int rating = 5;
        Book ratedBook = bookRatingService.rateBook(book.getBookId(), client.getClientId(), rating);

        ArgumentCaptor<BookRating> ratingCaptor = ArgumentCaptor.forClass(BookRating.class);
        verify(bookRatingRepository).save(ratingCaptor.capture());

        BookRating savedRating = ratingCaptor.getValue();
        assertEquals(book.getBookId(), savedRating.getBook().getBookId());
        assertEquals(client.getClientId(), savedRating.getClient().getClientId());
        assertEquals(rating, savedRating.getRating());

        assertEquals(book, ratedBook);
    }

    @Test
    public void rateBook_CorrectlyRatesBookWithExistingRating() {
        Book book = new Book("Test Book", 2022, 0.0);
        book.setBookId(1);
        book.setRatings(new HashSet<>());

        Client client = new Client("Test Client");
        client.setClientId(1);

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        BookRating existingRating = new BookRating();
        existingRating.setId(new BookRatingKey(book.getBookId(), client.getClientId()));
        existingRating.setBook(book);
        existingRating.setClient(client);
        existingRating.setRating(3);

        when(bookRatingRepository.findById(existingRating.getId())).thenReturn(Optional.of(existingRating));

        int rating = 5;
        Book ratedBook = bookRatingService.rateBook(book.getBookId(), client.getClientId(), rating);

        ArgumentCaptor<BookRating> ratingCaptor = ArgumentCaptor.forClass(BookRating.class);
        verify(bookRatingRepository).save(ratingCaptor.capture());

        BookRating savedRating = ratingCaptor.getValue();
        assertEquals(book.getBookId(), savedRating.getBook().getBookId());
        assertEquals(client.getClientId(), savedRating.getClient().getClientId());
        assertEquals(rating, savedRating.getRating());

        assertEquals(book, ratedBook);
    }

    @Test
    void rateBook_BookNotFoundThrowsEntityNotFoundException() {
        Book book = new Book("Test Book", 2022, 0.0);
        book.setBookId(1);

        Client client = new Client("Test Client");
        client.setClientId(1);

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> bookRatingService.rateBook(book.getBookId(), client.getClientId(), 5));

        assertEquals("Book not found", thrown.getMessage());
        verify(bookRepository).findById(book.getBookId());
        verifyNoMoreInteractions(clientRepository, bookRatingRepository);
    }

    @Test
    void rateBook_ClientNotFoundThrowsEntityNotFoundException() {
        Book book = new Book("Test Book", 2022, 0.0);
        book.setBookId(1);

        Client client = new Client("Test Client");
        client.setClientId(1);

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> bookRatingService.rateBook(book.getBookId(), client.getClientId(), 5));

        assertEquals("Client not found", thrown.getMessage());
        verify(bookRepository).findById(book.getBookId());
        verify(clientRepository).findById(client.getClientId());
        verifyNoMoreInteractions(bookRatingRepository);
    }
}