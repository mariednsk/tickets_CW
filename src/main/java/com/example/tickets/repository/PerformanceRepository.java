package com.example.tickets.repository;

import com.example.tickets.model.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    Page<Performance> findByGenreContainingIgnoreCase(String genre, Pageable pageable);

    Page<Performance> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Performance> findByGenreContainingIgnoreCaseAndTitleContainingIgnoreCase(
            String genre,
            String title,
            Pageable pageable
    );
}
