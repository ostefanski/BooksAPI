package com.task.books.controller;

import com.task.books.model.Book;
import com.task.books.request.BookRateRequest;
import com.task.books.service.BookRatingService;
import com.task.books.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookRatingService bookRatingService;

    @Autowired
    public BookController(BookService bookService, BookRatingService bookRatingService) {
        this.bookService = bookService;
        this.bookRatingService = bookRatingService;
    }

    // Get all books with optional filters
    @GetMapping("/filter")
    public List<Book> getBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Double rating) {
        return bookService.filterBooks(title, year, author, rating);
    }

    // Rate a book by client
    @PostMapping("/rate/{id}")
    public Book rateBook(@PathVariable Integer id, @RequestBody BookRateRequest bookRateRequest) {
        return bookRatingService.rateBook(id, bookRateRequest.getClientId(), bookRateRequest.getRating());
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Integer id, @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateBook(id, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"" + ex.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
    }
}
