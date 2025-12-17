package com.example.tickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seat")
@Getter
@Setter
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "row_label")
    private String rowLabel;

    @Column(name = "seat_number")
    private int seatNumber;

    private boolean occupied = false;

    @ManyToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    public Seat() {
    }

    public Seat(String rowLabel, int seatNumber, Performance performance) {
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.performance = performance;
        this.occupied = false;
    }
}