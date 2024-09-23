package com.task.books.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.books.model.*;
import com.task.books.repository.AuthorRepository;
import com.task.books.repository.BookRatingRepository;
import com.task.books.repository.BookRepository;
import com.task.books.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRatingRepository bookRatingRepository;

    private Integer bookId1;
    private Integer bookId2;
    private Integer clientId;

    @BeforeEach
    public void setUp() {
        bookRepository.deleteAll();
        clientRepository.deleteAll();
        authorRepository.deleteAll();
        bookRatingRepository.deleteAll();

        Author author = new Author("Alice Smith");

        authorRepository.save(author);

        Book testBook = new Book("Integration Test", 2022, 5.0);
        Book testBook2 = new Book("Integration Test2", 2023, 4.0);

        testBook.setAuthors(Set.of(author));

        bookRepository.save(testBook);
        bookRepository.save(testBook2);

        bookId1 = testBook.getBookId();
        bookId2 = testBook2.getBookId();

        Client testClient = new Client("John Doe");

        clientRepository.save(testClient);

        clientId = testClient.getClientId();

        BookRatingKey key1 = new BookRatingKey(testBook.getBookId(), testClient.getClientId());
        BookRatingKey key2 = new BookRatingKey(testBook2.getBookId(), testClient.getClientId());

        BookRating rating1 = new BookRating(key1, testBook, testClient, 5);
        BookRating rating2 = new BookRating(key2, testBook2, testClient, 4);

        bookRatingRepository.saveAll(List.of(rating1, rating2));
    }

    @Test
    public void getBooks_FilterByDefault() throws Exception {
        mockMvc.perform(get("/api/books/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Integration Test"))
                .andExpect(jsonPath("$[0].year").value(2022));
    }

    @Test
    public void getBooks_FilterByTitle() throws Exception{
        mockMvc.perform(get("/api/books/filter")
                        .param("title", "Integration Test2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Integration Test2"))
                .andExpect(jsonPath("$[0].year").value(2023));
    }

    @Test
    public void getBooks_FilterByYear() throws Exception{
        mockMvc.perform(get("/api/books/filter")
                        .param("year", "2022"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Integration Test"))
                .andExpect(jsonPath("$[0].year").value(2022));
    }

    @Test
    public void getBooks_FilterByAuthor() throws Exception{
        mockMvc.perform(get("/api/books/filter")
                        .param("author", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Integration Test"));
    }

    @Test
    public void getBooks_FilterByRating() throws Exception {
        mockMvc.perform(get("/api/books/filter")
                        .param("rating", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Integration Test"));
    }

    @Test
    public void getBooks_FilterByEveryFilter() throws Exception {
        mockMvc.perform(get("/api/books/filter")
                        .param("title", "Integration Test")
                        .param("year", "2022")
                        .param("author", "Alice")
                        .param("rating", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Integration Test"))
                .andExpect(jsonPath("$[0].year").value(2022))
                .andExpect(jsonPath("$[0].authors[0].name").value("Alice Smith"))
                .andExpect(jsonPath("$[0].ratings[0].rating").value(5));
    }

    @Test
    public void getBooks_FilterByMixedFiltersTitleAndRating() throws Exception {
        mockMvc.perform(get("/api/books/filter")
                        .param("title", "Integration")
                        .param("rating", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Integration Test"))
                .andExpect(jsonPath("$[1].title").value("Integration Test2"));
    }

    @Test
    public void rateBook_Success() throws Exception {
        mockMvc.perform(post("/api/books/rate/" + bookId2)
                .contentType("application/json")
                .content("{\"clientId\": \"" + clientId + "\", \"rating\": 3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test2"))
                .andExpect(jsonPath("$.year").value(2023))
                .andExpect(jsonPath("$.ratings[0].rating").value(3));
    }

    @Test
    public void addBook_ReturnsCreatedBook() throws Exception {
        Book newBook = new Book("New Integration Test Book", 2023, 0.0);
        String newBookJson = objectMapper.writeValueAsString(newBook);

        mockMvc.perform(post("/api/books")
                .contentType("application/json")
                .content(newBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Integration Test Book"))
                .andExpect(jsonPath("$.year").value(2023));

        List<Book> books = bookRepository.findAll();
        assert(books.stream().anyMatch(book -> "New Integration Test Book".equals(book.getTitle())));
    }

    @Test
    public void updateBook_Success() throws Exception {
        Book updatedBook = new Book("New Title", 2023, 5.0);
        String updatedBookJson = objectMapper.writeValueAsString(updatedBook);

        mockMvc.perform(put("/api/books/" + bookId1)
                        .contentType("application/json")
                        .content(updatedBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.year").value(2023))
                .andExpect(jsonPath("$.avgRating").value(5.0));
    }

    @Test
    public void updateBook_BookNotFound() throws Exception {
        Book updatedBook = new Book("New Title", 2023, 5.0);
        String updatedBookJson = objectMapper.writeValueAsString(updatedBook);

        mockMvc.perform(put("/api/books/999")
                        .contentType("application/json")
                        .content(updatedBookJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void getBookById_ReturnsBook() throws Exception {
        mockMvc.perform(get("/api/books/" + bookId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test"))
                .andExpect(jsonPath("$.year").value(2022));
    }

    @Test
    void deleteBook_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/books/" + bookId1))
                .andExpect(status().isNoContent());

        assert(bookRepository.findById(1).isEmpty());
    }
}
