package com.task.books.controller;

import com.task.books.model.Author;
import com.task.books.model.Book;
import com.task.books.model.BookRating;
import com.task.books.service.BookService;
import com.task.books.service.BookRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class BookControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Mock
    private BookRatingService bookRatingService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void getBooks_FilterByTitle() throws Exception {
        List<Book> books = List.of(new Book("Test Book", 2001, 0.0));
        when(bookService.filterBooks("Test Book", null, null, null)).thenReturn(books);

        mockMvc.perform(get("/api/books/filter")
                        .param("title", "Test Book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));

        verify(bookService).filterBooks("Test Book", null, null, null);
    }

    @Test
    public void getBooks_FilterByYear() throws Exception {
        List<Book> books = List.of(new Book("Test Book", 2001, 0.0));
        when(bookService.filterBooks(null, 2001, null, null)).thenReturn(books);

        mockMvc.perform(get("/api/books/filter")
                        .param("year", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].year").value(2001));

        verify(bookService).filterBooks(null, 2001, null, null);
    }

    @Test
    public void getBooks_FilterByAuthor() throws Exception {
        Book book = new Book("Test Book", 2001, 0.0);
        book.setAuthors(Set.of(new Author("Author Name")));
        List<Book> books = List.of(book);
        when(bookService.filterBooks(null, null, "Author Name", null)).thenReturn(books);

        mockMvc.perform(get("/api/books/filter")
                        .param("author", "Author Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].authors[0].name").value("Author Name"));

        verify(bookService).filterBooks(null, null, "Author Name", null);
    }

    @Test
    public void getBooks_FilterByRating() throws Exception {
        BookRating bookRating = new BookRating();
        bookRating.setRating(5);

        Book book = new Book("Test Book", 2001, 0.0);
        book.setRatings(Set.of(bookRating));
        List<Book> books = List.of(book);

        when(bookService.filterBooks(null, null, null, 4.0)).thenReturn(books);

        mockMvc.perform(get("/api/books/filter")
                        .param("rating", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].ratings[0].rating").value(5));

        verify(bookService).filterBooks(null, null, null, 4.0);
    }

    @Test
    public void getBooks_FilterByEveryFilter() throws Exception{
        BookRating bookRating = new BookRating();
        bookRating.setRating(5);

        Book book = new Book("Test Book", 2001, 0.0);
        book.setAuthors(Set.of(new Author("Author Name")));
        book.setRatings(Set.of(bookRating));
        List<Book> books = List.of(book);

        when(bookService.filterBooks("Test Book", 2001, "Author Name", 4.0)).thenReturn(books);

        mockMvc.perform(get("/api/books/filter")
                        .param("title", "Test Book")
                        .param("year", "2001")
                        .param("author", "Author Name")
                        .param("rating", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].year").value(2001))
                .andExpect(jsonPath("$[0].authors[0].name").value("Author Name"))
                .andExpect(jsonPath("$[0].ratings[0].rating").value(5));

        verify(bookService).filterBooks("Test Book", 2001, "Author Name", 4.0);
    }

    @Test
    public void getBooks_FilterByMixedFiltersYearAndTitle() throws Exception {
        List<Book> books = List.of(new Book("Test Book", 2001, 0.0));
        when(bookService.filterBooks("Test Book", 2001, null, null)).thenReturn(books);

        mockMvc.perform(get("/api/books/filter")
                        .param("title", "Test Book")
                        .param("year", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].year").value(2001));

        verify(bookService).filterBooks("Test Book", 2001, null, null);
    }

    @Test
    public void getBooks_FilterByDefault() throws Exception{
        List<Book> books = List.of(new Book("Test Book", 2001, 0.0));
        when(bookService.filterBooks(null, null, null, null)).thenReturn(books);

        mockMvc.perform(get("/api/books/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));

        verify(bookService).filterBooks(null, null, null, null);
    }


    @Test
    public void rateBook_Success() throws Exception {
        Book book = new Book("Test Book", 2001, 0.0);
        when(bookRatingService.rateBook(1, 1, 5)).thenReturn(book);

        String requestBody = "{\"clientId\": 1, \"rating\": 5}";

        mockMvc.perform(post("/api/books/rate/1")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.year").value(2001));

        verify(bookRatingService).rateBook(1, 1, 5);
    }

    @Test
    public void getBookById() throws Exception {
        Book book = new Book("Test Book", 2001, 0.0);
        when(bookService.getBookById(1)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.year").value(2001));

        verify(bookService).getBookById(1);
    }

    @Test
    public void addBook() throws Exception {
        Book book = new Book("New Book", 2021, 0.0);
        when(bookService.addBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content("{\"title\":\"New Book\", \"year\":2021}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.year").value(2021));

        verify(bookService).addBook(any(Book.class));
    }

    @Test
    public void deleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(1);
    }
}