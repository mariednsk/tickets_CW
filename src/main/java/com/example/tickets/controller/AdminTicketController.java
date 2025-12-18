package com.example.tickets.controller;

import com.example.tickets.model.AppUser;
import com.example.tickets.model.Performance;
import com.example.tickets.model.Seat;
import com.example.tickets.model.Ticket;
import com.example.tickets.repository.UserRepository;  // ← твой репозиторий
import com.example.tickets.repository.PerformanceRepository;
import com.example.tickets.repository.SeatRepository;
import com.example.tickets.repository.TicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/tickets")
public class AdminTicketController {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;
    private final UserRepository appUserRepository;  // ← AppUser

    public AdminTicketController(TicketRepository ticketRepository,
                                 SeatRepository seatRepository,
                                 PerformanceRepository performanceRepository,
                                 UserRepository appUserRepository) {
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.performanceRepository = performanceRepository;
        this.appUserRepository = appUserRepository;
    }

    // Список всех билетов
    @GetMapping
    public String listTickets(Model model) {
        List<Ticket> tickets = ticketRepository.findAll();
        tickets.sort((t1, t2) -> t2.getPerformance().getDate().compareTo(t1.getPerformance().getDate()));
        model.addAttribute("tickets", tickets);
        return "admin_tickets";
    }

    // Удаление билета
    @PostMapping("/delete/{id}")
    public String deleteTicket(@PathVariable Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        Seat seat = ticket.getSeat();
        if (seat != null) {
            seat.setOccupied(false);
            seatRepository.save(seat);
        }
        ticketRepository.deleteById(id);
        return "redirect:/admin/tickets";
    }

    // Форма редактирования
    @GetMapping("/edit/{id}")
    public String editTicketForm(@PathVariable Long id, Model model) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();

        List<Performance> performances = performanceRepository.findAll();
        List<AppUser> users = appUserRepository.findAll();

        // Все места текущего спектакля (занятые и свободные)
        List<Seat> seatsOfPerformance = seatRepository.findByPerformanceId(ticket.getPerformance().getId());

        model.addAttribute("ticket", ticket);
        model.addAttribute("performances", performances);
        model.addAttribute("users", users);
        model.addAttribute("seatsOfPerformance", seatsOfPerformance);

        return "admin_ticket_edit";
    }

    @PostMapping("/edit/{id}")
    public String editTicket(@PathVariable Long id,
                             @RequestParam String buyerName,
                             @RequestParam Long performanceId,
                             @RequestParam Long seatId,
                             @RequestParam(required = false) Long userId) {

        Ticket ticket = ticketRepository.findById(id).orElseThrow();

        // Освобождаем старое место
        Seat oldSeat = ticket.getSeat();
        if (oldSeat != null) {
            oldSeat.setOccupied(false);
            seatRepository.save(oldSeat);
        }

        // Новый спектакль
        Performance newPerformance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("Спектакль не найден"));

        // ← ВОТ ЭТА СТРОКА — главное исправление!
        ticket.setEventName(newPerformance.getTitle());  // Обновляем название!

        ticket.setBuyerName(buyerName);
        ticket.setPerformance(newPerformance);

        // Новое место
        Seat newSeat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Место не найдено"));
        newSeat.setOccupied(true);
        seatRepository.save(newSeat);
        ticket.setSeat(newSeat);

        // Пользователь
        if (userId != null) {
            AppUser user = appUserRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
            ticket.setUser(user);
        } else {
            ticket.setUser(null);
        }

        ticketRepository.save(ticket);
        return "redirect:/admin/tickets";
    }
}