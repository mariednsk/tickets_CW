package com.example.tickets.repository;

import com.example.tickets.model.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    List<Actor> findByFullNameContainingIgnoreCase(String name);

    Page<Actor> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}