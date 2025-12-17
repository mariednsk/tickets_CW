package com.example.tickets.repository;

import com.example.tickets.model.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    Page<Performance> findByGenreContainingIgnoreCase(String genre, Pageable pageable);

    Page<Performance> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Performance> findByGenreContainingIgnoreCaseAndTitleContainingIgnoreCase(
            String genre,
            String title,
            Pageable pageable
    );

    @Query("""
    select distinct p
    from Performance p
    join p.actors a
    where lower(a.fullName) like lower(concat('%', :actor, '%'))
""")
    Page<Performance> findByActorName(@Param("actor") String actor, Pageable pageable);

    @Query("""
    select distinct p
    from Performance p
    join p.actors a
    where lower(p.title) like lower(concat('%', :search, '%'))
      and lower(a.fullName) like lower(concat('%', :actor, '%'))
""")
    Page<Performance> findByTitleAndActorName(@Param("search") String search,
                                              @Param("actor") String actor,
                                              Pageable pageable);

    @Query("""
    select distinct p
    from Performance p
    join p.actors a
    where lower(p.genre) like lower(concat('%', :genre, '%'))
      and lower(a.fullName) like lower(concat('%', :actor, '%'))
""")
    Page<Performance> findByGenreAndActorName(@Param("genre") String genre,
                                              @Param("actor") String actor,
                                              Pageable pageable);

    @Query("""
    select distinct p
    from Performance p
    join p.actors a
    where lower(p.genre) like lower(concat('%', :genre, '%'))
      and lower(p.title) like lower(concat('%', :search, '%'))
      and lower(a.fullName) like lower(concat('%', :actor, '%'))
""")
    Page<Performance> findByGenreTitleActor(@Param("genre") String genre,
                                            @Param("search") String search,
                                            @Param("actor") String actor,
                                            Pageable pageable);
}
