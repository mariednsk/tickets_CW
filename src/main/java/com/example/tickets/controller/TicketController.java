package com.example.tickets.controller;

import com.example.tickets.model.Ticket;
import com.example.tickets.repository.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.tickets.model.AppUser;
import com.example.tickets.model.Seat;
import com.example.tickets.repository.SeatRepository;
import com.example.tickets.repository.UserRepository;
import org.springframework.security.core.Authentication;


@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public TicketController(TicketRepository ticketRepository,
                            SeatRepository seatRepository,
                            UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    /** Список билетов + форма добавления (форма видна только ADMIN в шаблоне) */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("tickets", ticketRepository.findAll());
        // объект для формы добавления
        model.addAttribute("ticket", new Ticket());
        return "tickets";
    }

    /** Создание билета — только ADMIN */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("ticket") Ticket ticket,
                      BindingResult bindingResult,
                      Model model) {

        if (bindingResult.hasErrors()) {
            // Перерисовываем список с ошибками
            model.addAttribute("tickets", ticketRepository.findAll());
            return "tickets";
        }
        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }

    /** Страница редактирования — только ADMIN */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Билет не найден: " + id));
        model.addAttribute("ticket", ticket);
        return "edit_ticket";
    }

    /** Сохранение изменения — только ADMIN */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String saveEdited(@PathVariable Long id,
                             @Valid @ModelAttribute("ticket") Ticket ticket,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "edit_ticket";
        }
        // фиксируем правильный id
        ticket.setId(id);
        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }

    /** Удаление — только ADMIN */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        if (!ticketRepository.existsById(id)) {
            return "redirect:/tickets";
        }
        ticketRepository.deleteById(id);
        return "redirect:/tickets";
    }

    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Билет не найден: " + id));

        AppUser currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // отменять может только владелец или админ
        if (!isAdmin && (ticket.getUser() == null || !ticket.getUser().getId().equals(currentUser.getId()))) {
            return "redirect:/profile?forbidden#tickets";
        }

        Seat seat = ticket.getSeat();
        if (seat != null) {
            seat.setOccupied(false);
            seatRepository.save(seat);
        }

        ticketRepository.delete(ticket);

        return "redirect:/profile?canceled#tickets";
    }

}




