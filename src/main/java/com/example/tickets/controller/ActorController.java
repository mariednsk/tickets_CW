package com.example.tickets.controller;

import com.example.tickets.model.Actor;
import com.example.tickets.repository.ActorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ActorController {

    private final ActorRepository actorRepo;

    public ActorController(ActorRepository actorRepo) {
        this.actorRepo = actorRepo;
    }

    @GetMapping("/actors")
    public String actorsPage(@RequestParam(defaultValue = "") String q, Model model) {
        var actors = q.isBlank()
                ? actorRepo.findAll()
                : actorRepo.findByFullNameContainingIgnoreCase(q);

        model.addAttribute("q", q);
        model.addAttribute("actors", actors);
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
