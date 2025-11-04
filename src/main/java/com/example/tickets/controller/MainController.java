package com.example.tickets.controller;

import com.example.tickets.repository.TicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final TicketRepository ticketRepository;

    public MainController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tickets", ticketRepository.findAll());
        return "index"; // index.html
    }
}
