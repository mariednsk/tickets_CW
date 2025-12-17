package com.example.tickets.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ticket")
@Getter
@Setter
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название мероприятия не может быть пустым")
    @Size(max = 100, message = "Название мероприятия слишком длинное")
    private String eventName;

    @NotBlank(message = "Имя покупателя не может быть пустым")
    @Size(max = 50, message = "Имя покупателя слишком длинное")
    private String buyerName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;  // Связь с местом

    public Ticket() {}
}