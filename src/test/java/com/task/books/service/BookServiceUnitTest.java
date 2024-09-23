package com.task.books.service;

import com.task.books.model.Author;
import com.task.books.model.Book;
import com.task.books.model.BookRating;
import com.task.books.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceUnitTest {

    @Mock
    private BookRepository bookRepository;

    private BookService bookService;

    @BeforeEach
    public void setUp() {
        bookService = new BookService(bookRepository);
    }

    @Test
    public void filterBooksByTitle() {
        String title = "Test Book";
        List<Book> allBooks = List.of(new Book("Test Book", 2001, 0.0), new Book("Other Book", 1999, 0.0));

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> actualBooks = bookService.filterBooks(title, null, null, null);

        assertEquals(1, actualBooks.size());
        assertEquals("Test Book", actualBooks.get(0).getTitle());
        verify(bookRepository).findAll();
    }

    @Test
    public void filterBooksByYear() {
        Integer year = 2001;
        List<Book> allBooks = List.of(new Book("Test Book", 2001, 0.0), new Book("Other Book", 1999, 0.0));

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> actualBooks = bookService.filterBooks(null, year, null, null);

        assertEquals(1, actualBooks.size());
        assertEquals(year, actualBooks.get(0).getYear());
        verify(bookRepository).findAll();
    }

    @Test
    public void filterBooksByAuthor() {
        String authorName = "test";
        Author author = new Author(authorName);
        Book book = new Book("Test Book", 2001, 0.0);
        book.setAuthors(Set.of(author));
        Book book2 = new Book("Other Book", 1999, 0.0);
        book2.setAuthors(Set.of(new Author("other")));

        List<Book> allBooks = List.of(book, book2);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> actualBooks = bookService.filterBooks(null, null, authorName, null);

        assertEquals(1, actualBooks.size());
        assertTrue(actualBooks.get(0).getAuthors().stream().anyMatch(a -> a.getName().equalsIgnoreCase(authorName)));
        verify(bookRepository).findAll();
    }


    @Test
    public void filterBooksByRating() {
        Double rating = 3.0;
        BookRating bookRating = new BookRating();
        bookRating.setRating(5);

        BookRating bookRating1 = new BookRating();
        bookRating1.setRating(3);

        Book book = new Book("Test Book", 2001, 5.0);
        book.setRatings(Set.of(bookRating));

        Book book1 = new Book("Test Book1", 2001, 3.0);
        book1.setRatings(Set.of(bookRating1));

        List<Book> allBooks = List.of(book,book1);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> actualBooks = bookService.filterBooks(null, null, null, rating);

        assertEquals(2, actualBooks.size());
        assertTrue(actualBooks.get(0).getRatings().stream().mapToDouble(BookRating::getRating).average().orElse(0) >= rating);
        verify(bookRepository).findAll();
    }

    @Test
    public void filterBooksByDefault() {
        List<Book> expectedBooks = List.of(new Book("Test Book", 2001, 0.0), new Book("Other Book", 1999, 0.0));

        when(bookRepository.findAll()).thenReturn(expectedBooks);

        List<Book> actualBooks = bookService.filterBooks(null, null, null, null);

        assertEquals(expectedBooks, actualBooks);
        assertEquals(2, actualBooks.size());
        verify(bookRepository).findAll();
    }

    @Test
    public void filterBooksByEveryFilter() {
        String title = "Test Book";
        Integer year = 2001;
        String authorName = "test";
        Double rating = 3.0;

        Author author = new Author(authorName);
        BookRating bookRating = new BookRating();
        bookRating.setRating(5);

        BookRating bookRating2 = new BookRating();
        bookRating2.setRating(2);

        Book book = new Book("Test Book", 2001, 5.0);
        book.setAuthors(Set.of(author));
        book.setRatings(Set.of(bookRating));

        Book book2 = new Book("Other Book", 1999, 2.0);
        book2.setAuthors(Set.of(new Author("other")));
        book2.setRatings(Set.of(bookRating2));

        List<Book> allBooks = List.of(book, book2);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> actualBooks = bookService.filterBooks(title, year, authorName, rating);

        assertEquals(1, actualBooks.size());
        assertEquals(title, actualBooks.get(0).getTitle());
        assertEquals(year, actualBooks.get(0).getYear());
        assertTrue(actualBooks.get(0).getAuthors().stream().anyMatch(a -> a.getName().equalsIgnoreCase(authorName)));
        assertTrue(actualBooks.get(0).getRatings().stream().mapToDouble(BookRating::getRating).average().orElse(0) >= rating);
        verify(bookRepository).findAll();

    }

    @Test
    public void filterBooksByMixedFiltersTitleAndRating() {
        String title = "Test Book";
        Double rating = 3.0;

        BookRating bookRating = new BookRating();
        bookRating.setRating(5);

        BookRating bookRating2 = new BookRating();
        bookRating2.setRating(3);

        Book book = new Book("Test Book", 2001, 5.0);
        book.setRatings(Set.of(bookRating));

        Book book2 = new Book("Other Book", 1999, 3.0);
        book2.setRatings(Set.of(bookRating2));

        List<Book> allBooks = List.of(book, book2);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> actualBooks = bookService.filterBooks(title, null, null, rating);

        assertEquals(1, actualBooks.size());
        assertEquals(title, actualBooks.get(0).getTitle());
        assertTrue(actualBooks.get(0).getRatings().stream().mapToDouble(BookRating::getRating).average().orElse(0) >= rating);
        verify(bookRepository).findAll();
    }

    @Test
    void getBookById() {
        Book book = new Book("Harry Potter", 1997, 0.0);
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookById(book.getBookId());

        assertNotNull(foundBook);
        assertEquals("Harry Potter", foundBook.getTitle());
        verify(bookRepository).findById(book.getBookId());
    }

    @Test
    void addBook() {
        Book book = new Book("Harry Potter", 1997, 0.0);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book savedBook = bookService.addBook(book);

        assertNotNull(savedBook);
        assertEquals("Harry Potter", savedBook.getTitle());
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_Success() {
        Book existingBook = new Book("Existing Title", 2022, 4.0);
        existingBook.setBookId(1);

        Book updatedBookDetails = new Book("Updated Title", 2023, 4.5);

        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        Book result = bookService.updateBook(1, updatedBookDetails);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(2023, result.getYear());
        assertEquals(4.5, result.getAvgRating());

        verify(bookRepository).findById(1);
        verify(bookRepository).save(existingBook);
    }

    @Test
    void updateBook_BookNotFound() {
        Book updatedBookDetails = new Book("Updated Title", 2023, 4.5);

        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.updateBook(999, updatedBookDetails);
        });

        assertEquals("Book not found", exception.getMessage());

        verify(bookRepository).findById(999);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook() {
        Book book = new Book("Harry Potter", 1997, 0.0);

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        bookService.deleteBook(book.getBookId());

        verify(bookRepository).deleteById(book.getBookId());

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.empty());

        assertTrue(bookRepository.findById(book.getBookId()).isEmpty());
    }
}