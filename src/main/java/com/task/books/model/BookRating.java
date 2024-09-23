package com.task.books.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Setter
@Getter
@Entity
@Table(name = "book_rating")
@Check(constraints = "rating BETWEEN 1 AND 5")
@JsonIgnoreProperties("book")  // Ignore the back-reference to book during serialization
public class BookRating {

    @EmbeddedId
    private BookRatingKey id;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @MapsId("clientId")
    @JoinColumn(name = "client_id")
    private Client client;

    private int rating;

    public BookRating(BookRatingKey bookRatingKey ,Book book, Client client, int rating) {
        this.id = bookRatingKey;
        this.book = book;
        this.client = client;
        this.rating = rating;
    }

    public BookRating() {

    }
}

