package com.task.books.data;

import com.task.books.model.*;
import com.task.books.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Profile("!test")
@Component
public class DatabaseMockData implements CommandLineRunner{
    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final ClientRepository clientRepository;

    private final BookRatingRepository bookRatingRepository;

    @Autowired
    public DatabaseMockData(BookRepository bookRepository, AuthorRepository authorRepository, ClientRepository clientRepository, BookRatingRepository bookRatingRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.clientRepository = clientRepository;
        this.bookRatingRepository = bookRatingRepository;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            Author author1 = new Author("J.K. Rowling");
            Author author2 = new Author("George R.R. Martin");
            Author author3 = new Author("J.R.R. Tolkien");
            Author author4 = new Author("Christopher Nolan");
            Author author5 = new Author("George Orwell");
            Author author6 = new Author("Agatha Christie");

            authorRepository.saveAll(List.of(author1, author2, author3, author4, author5, author6));

            Book book1 = new Book("Harry Potter and the Sorcerer's Stone", 1997, 4.5);
            Book book2 = new Book("A Song of Ice and Fire", 1996, 4.0);
            Book book3 = new Book("The Lord of the Rings", 1954, 3.0);
            Book book4 = new Book("Inception", 2010, 2.0);
            Book book5 = new Book("Harry Potter and the Chamber of Secrets", 1998, 0.0);
            Book book6 = new Book("1984", 1949, 5.0);
            Book book7 = new Book("Murder on the Orient Express", 1949, 0.0);
            Book book8 = new Book("The Hobbit", 1937, 4.0);

            book1.setAuthors(Set.of(author1));
            book2.setAuthors(Set.of(author2));
            book3.setAuthors(Set.of(author3));
            book4.setAuthors(Set.of(author4));
            book5.setAuthors(Set.of(author1));
            book6.setAuthors(Set.of(author5));
            book7.setAuthors(Set.of(author6));
            book8.setAuthors(Set.of(author3));

            bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6, book7, book8));

            Client client1 = new Client("John Doe");
            Client client2 = new Client("Jane Smith");
            Client client3 = new Client("Alice Johnson");

            clientRepository.saveAll(List.of(client1, client2, client3));

            BookRating rating1 = new BookRating(new BookRatingKey(book1.getBookId(), client1.getClientId()), book1, client1, 5);
            BookRating rating2 = new BookRating(new BookRatingKey(book1.getBookId(), client2.getClientId()), book1, client2, 4);
            BookRating rating3 = new BookRating(new BookRatingKey(book2.getBookId(), client2.getClientId()), book2, client2, 4);
            BookRating rating4 = new BookRating(new BookRatingKey(book3.getBookId(), client1.getClientId()), book3, client1, 3);
            BookRating rating5 = new BookRating(new BookRatingKey(book4.getBookId(), client3.getClientId()), book4, client3, 2);
            BookRating rating6 = new BookRating(new BookRatingKey(book6.getBookId(), client1.getClientId()), book6, client1, 5);
            BookRating rating7 = new BookRating(new BookRatingKey(book8.getBookId(), client3.getClientId()), book8, client3, 4);

            bookRatingRepository.saveAll(List.of(rating1, rating2, rating3, rating4, rating5, rating6, rating7));

            System.out.println("Mock data loaded into the database.");
        } else {
            System.out.println("Mock data already exists.");
        }
    }
}
