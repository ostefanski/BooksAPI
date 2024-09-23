package com.task.books.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "name", nullable = false)
    private String name;

    // Many-to-Many relationship with Book through BookRating
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonIgnore // Ignore the back-reference to BookRating during serialization
    private Set<BookRating> ratings;

    public Client(String name) {
        this.name = name;
    }

    public Client() {

    }
}

