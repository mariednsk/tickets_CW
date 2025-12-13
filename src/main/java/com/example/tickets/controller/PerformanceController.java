package com.example.tickets.controller;

import com.example.tickets.model.Performance;
import com.example.tickets.repository.PerformanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PerformanceController {

    private final PerformanceRepository repo;

    public PerformanceController(PerformanceRepository repo) {
        this.repo = repo;
    }

    // Страница афиши (отдаёт HTML; карточки подгружаются AJAX'ом)
    @GetMapping("/performances")
    public String performancesPage(Model model) {
        // передаём список жанров для фильтра (можно сделать динамически; пока список заранее)
        model.addAttribute("genres", new String[] {
                "комедия","драма","мюзикл","трагедия","балет","детский спектакль","опера"
        });
        return "performances";
    }

    // Детальная страница (минималистично, но красиво)
    @GetMapping("/performances/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Performance p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Performance not found: " + id));
        model.addAttribute("p", p);
        return "performance_detail";
    }

    @GetMapping("/performances/{id}/buy")
    public String buy(@PathVariable Long id, Model model) {
        Performance p = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Performance not found"));

        model.addAttribute("p", p);
        return "buy_ticket"; // имя HTML
    }


    // --------------------
    // JSON API для AJAX: /api/performances?page=0&size=6&genre=комедия
    @GetMapping("/api/performances")
    @ResponseBody
    public Page<Performance> apiPerformances(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "3") int size,
                                             @RequestParam(defaultValue = "") String genre,
                                             @RequestParam(defaultValue = "") String search) {

        PageRequest pr = PageRequest.of(page, size);

        // оба пустые → без фильтра
        if (genre.isBlank() && search.isBlank()) {
            return repo.findAll(pr);
        }

        // только жанр
        if (!genre.isBlank() && search.isBlank()) {
            return repo.findByGenreContainingIgnoreCase(genre, pr);
        }

        // только поиск
        if (genre.isBlank() && !search.isBlank()) {
            return repo.findByTitleContainingIgnoreCase(search, pr);
        }

        // жанр + поиск одновременно
        return repo.findByGenreContainingIgnoreCaseAndTitleContainingIgnoreCase(genre, search, pr);
    }


}
