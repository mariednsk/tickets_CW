package com.example.tickets.controller;

import com.example.tickets.model.Performance;
import com.example.tickets.model.Ticket;
import com.example.tickets.repository.UserRepository;
import com.example.tickets.repository.PerformanceRepository;
import com.example.tickets.repository.TicketRepository;
import com.example.tickets.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/stats")
public class AdminStatsController {

    private final UserRepository userRepo;
    private final TicketRepository ticketRepo;
    private final PerformanceRepository performanceRepo;

    public AdminStatsController(UserRepository userRepo, TicketRepository ticketRepo, PerformanceRepository performanceRepo) {
        this.userRepo = userRepo;
        this.ticketRepo = ticketRepo;
        this.performanceRepo = performanceRepo;
    }

    @GetMapping
    public String stats(Model model) {
        long totalUsers = userRepo.count();
        long totalTickets = ticketRepo.count();

        List<Ticket> allTickets = ticketRepo.findAll();

        // Исправлено: BigDecimal → double через .doubleValue()
        double totalRevenue = allTickets.stream()
                .filter(t -> t.getPerformance() != null)
                .mapToDouble(t -> t.getPerformance().getPrice().doubleValue())
                .sum();

        // Количество билетов по спектаклям (остаётся без изменений)
        Map<String, Long> ticketsByPerformance = allTickets.stream()
                .filter(t -> t.getPerformance() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getPerformance().getTitle(),
                        Collectors.counting()
                ));

        // Выручка по спектаклям — тоже через .doubleValue()
        Map<String, Double> revenueByPerformance = allTickets.stream()
                .filter(t -> t.getPerformance() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getPerformance().getTitle(),
                        Collectors.summingDouble(t -> t.getPerformance().getPrice().doubleValue())
                ));

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("ticketsByPerformance", ticketsByPerformance);
        model.addAttribute("revenueByPerformance", revenueByPerformance);

        return "admin_stats";
    }



}