package com.example.tickets.controller;

import com.example.tickets.model.Actor;
import com.example.tickets.repository.ActorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ActorController {

    private final ActorRepository actorRepo;

    public ActorController(ActorRepository actorRepo) {
        this.actorRepo = actorRepo;
    }

    // Список артистов с поиском и пагинацией — как в спектаклях
    @GetMapping("/actors")
    public String actorsPage(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Actor> actorPage;
        if (q != null && !q.isBlank()) {
            actorPage = actorRepo.findByFullNameContainingIgnoreCase(q, pageable);
        } else {
            actorPage = actorRepo.findAll(pageable);
        }

        model.addAttribute("actors", actorPage);
        model.addAttribute("q", q);

        return "actors";
    }

    @GetMapping("/actors/{id}")
    public String actorDetail(@PathVariable Long id, Model model) {
        Actor a = actorRepo.findById(id).orElseThrow();
        model.addAttribute("a", a);
        model.addAttribute("performances", a.getPerformances());
        return "actor_detail";
    }
}