package com.task.books.model;

import jakarta.persistence.*;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Setter
@Getter
public class BookRatingKey implements Serializable {
    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "client_id")
    private Integer clientId;

    // Implement equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRatingKey that = (BookRatingKey) o;
        return Objects.equals(bookId, that.bookId) &&
                Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, clientId);
    }

    public BookRatingKey(Integer bookId, Integer clientId) {
        this.bookId = bookId;
        this.clientId = clientId;
    }
    // Default constructor required by JPA
    public BookRatingKey() {
    }
}
