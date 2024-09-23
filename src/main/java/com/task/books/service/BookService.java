package com.task.books.service;

import com.task.books.model.Author;
import com.task.books.model.Book;
import com.task.books.model.BookRating;
import com.task.books.repository.AuthorRepository;
import com.task.books.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Find books with optional filters
    public List<Book> filterBooks(String title, Integer year, String author, Double rating) {
        List<Book> books = bookRepository.findAll();
        if (title != null && !title.isEmpty()) {
            books = books.stream().filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase())).collect(Collectors.toList());
        }
        if (year != null) {
            books = books.stream().filter(book -> book.getYear().equals(year)).collect(Collectors.toList());
        }
        if (author != null && !author.isEmpty()) {
            books = books.stream().filter(book -> book.getAuthors().stream().anyMatch(a -> a.getName().toLowerCase().contains(author.toLowerCase()))).collect(Collectors.toList());
        }
        if (rating != null) {
            books = books.stream().filter(book -> book.getRatings().stream().mapToDouble(BookRating::getRating).average().orElse(0) >= rating).collect(Collectors.toList());
        }
        return books;
    }

    public Book getBookById(Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Integer id, Book bookDetails) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setYear(bookDetails.getYear());
        existingBook.setAvgRating(bookDetails.getAvgRating());

        return bookRepository.save(existingBook);
    }

    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }
}
