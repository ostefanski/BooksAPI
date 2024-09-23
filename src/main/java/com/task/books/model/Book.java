package com.task.books.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "published_year", nullable = false)
    private Integer year;

    @Column(name = "average_rating", columnDefinition = "DOUBLE PRECISION DEFAULT 0.0", nullable = false)
    private Double avgRating = 0.0;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors;

    // Many-to-Many relationship with Client through BookRating
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<BookRating> ratings;

    public Book(String title, Integer year, Double avgRating) {
        this.title = title;
        this.year = year;
        this.avgRating = avgRating;
    }

    // required by JPA
    public Book() {

    }
}
