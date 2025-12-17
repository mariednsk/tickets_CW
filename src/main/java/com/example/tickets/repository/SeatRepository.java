package com.example.tickets.repository;

import com.example.tickets.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByPerformanceId(Long performanceId);

    Seat findByPerformanceIdAndRowLabelAndSeatNumber(
            Long performanceId,
            String rowLabel,
            int seatNumber
    );
    void deleteByPerformanceId(Long performanceId);

}