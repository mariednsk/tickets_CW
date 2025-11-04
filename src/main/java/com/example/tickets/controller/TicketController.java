package com.example.tickets.controller;

import com.example.tickets.model.Ticket;
import com.example.tickets.repository.TicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public String listTickets(Model model) {
        model.addAttribute("tickets", ticketRepository.findAll());
        model.addAttribute("ticket", new Ticket());
        return "tickets";
    }

    @PostMapping("/add")
    public String addTicket(@ModelAttribute Ticket ticket, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tickets", ticketRepository.findAll());
            return "tickets";
        }
        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }

    @PostMapping("/delete/{id}")
    public String deleteTicket(@PathVariable Long id) {
        ticketRepository.deleteById(id);
        return "redirect:/tickets";
    }

    @GetMapping("/edit/{id}")
    public String editTicketForm(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", ticketRepository.findById(id).orElseThrow());
        return "edit_ticket";
    }

    @PostMapping("/edit/{id}")
    public String saveEditedTicket(@PathVariable Long id, @ModelAttribute Ticket ticket, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_ticket";
        }
        ticket.setId(id);
        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }
}
