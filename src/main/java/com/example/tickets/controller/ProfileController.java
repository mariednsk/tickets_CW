package com.example.tickets.controller;

import com.example.tickets.model.AppUser;
import com.example.tickets.model.Performance;
import com.example.tickets.model.Ticket;
import com.example.tickets.repository.TicketRepository;
import com.example.tickets.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public ProfileController(UserRepository userRepository, TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }


    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Избранные спектакли
        List<Performance> favorites = user.getFavoritePerformances().stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // по дате, новые сверху
                .toList();

        // Купленные билеты (с сортировкой по дате спектакля)
        List<Ticket> tickets = ticketRepository.findByUser(user).stream()
                .sorted((t1, t2) -> t2.getPerformance().getDate().compareTo(t1.getPerformance().getDate()))
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("favorites", favorites);
        model.addAttribute("tickets", tickets);

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication,
                                @RequestParam(defaultValue = "") String fullName,
                                @RequestParam(defaultValue = "") String phone,
                                @RequestParam(defaultValue = "") String email) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        AppUser user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setFullName(fullName.trim());
        user.setPhone(phone.trim());
        user.setEmail(email.trim());

        userRepository.save(user);
        return "redirect:/profile?saved";
    }


}