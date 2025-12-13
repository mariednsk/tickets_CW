package com.example.tickets.loader;

import com.example.tickets.model.Performance;
import com.example.tickets.repository.PerformanceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class PerformanceDataLoader implements CommandLineRunner {

    private final PerformanceRepository repo;

    public PerformanceDataLoader(PerformanceRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {

        // если есть хоть одна запись — не загружать
        if (repo.count() > 0) return;

        repo.save(new Performance(null, "Ромео и Джульетта",
                "Знаменитая трагедия Уильяма Шекспира о любви и судьбе.",
                LocalDate.now().plusDays(5),
                LocalTime.of(19, 0),
                "трагедия",
                new BigDecimal("1500.00"),
                "performances/romeo_juliet.jpg"));

        repo.save(new Performance(null, "Лебединое озеро",
                "Великий балет П. И. Чайковского в классической постановке.",
                LocalDate.now().plusDays(7),
                LocalTime.of(19, 0),
                "балет",
                new BigDecimal("2500.00"),
                "performances/swan_lake.jpg"));

        repo.save(new Performance(null, "Кармен",
                "Опера Жоржа Бизе в трёх действиях.",
                LocalDate.now().plusDays(10),
                LocalTime.of(19, 30),
                "опера",
                new BigDecimal("2200.00"),
                "performances/carmen.jpg"));

        repo.save(new Performance(null, "Ревизор",
                "Комедия Н. В. Гоголя о чиновниках и мнимом ревизоре.",
                LocalDate.now().plusDays(12),
                LocalTime.of(18, 0),
                "комедия",
                new BigDecimal("1300.00"),
                "performances/revizor.jpg"));

        repo.save(new Performance(null, "Щелкунчик",
                "Балет П. И. Чайковского — новогодняя сказка.",
                LocalDate.now().plusDays(14),
                LocalTime.of(17, 0),
                "балет",
                new BigDecimal("2400.00"),
                "performances/shelkunchik.jpeg"));

        repo.save(new Performance(null, "Гамлет",
                "Трагедия Шекспира о мести и судьбе.",
                LocalDate.now().plusDays(16),
                LocalTime.of(20, 0),
                "трагедия",
                new BigDecimal("1800.00"),
                "performances/gamlet.jpg"));
    }
}
