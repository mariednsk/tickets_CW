package com.example.tickets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "actor")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length = 120)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String photo;

    @JsonIgnore
    @ManyToMany(mappedBy = "actors")
    private Set<Performance> performances = new HashSet<>();
}
