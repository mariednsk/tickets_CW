package com.example.tickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
@Entity
@Table(name = "performance")
public class Performance {

    // getters / setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate date;
    private LocalTime time;
    private String genre;
    private BigDecimal price;

    // Относительный путь до файла в static/images/performances/
    private String image;

    // Геттер и сеттер (если у тебя нет Lombok для этого поля)
    @Setter
    @Getter
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "favorite_performance",
            joinColumns = @JoinColumn(name = "performance_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AppUser> favoritedBy = new HashSet<>();



    public Performance(Long id, String title, String description,
                       LocalDate date, LocalTime time, String genre,
                       BigDecimal price, String image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.genre = genre;
        this.price = price;
        this.image = image;

    }

    public Performance() {

    }
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "performance_actor",
            joinColumns = @JoinColumn(name = "performance_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors = new HashSet<>();
}
