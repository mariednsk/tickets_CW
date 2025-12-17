package com.example.tickets.controller;

import com.example.tickets.model.Performance;
import com.example.tickets.model.Seat;
import com.example.tickets.model.Ticket;
import com.example.tickets.model.AppUser;
import com.example.tickets.repository.PerformanceRepository;
import com.example.tickets.repository.SeatRepository;
import com.example.tickets.repository.TicketRepository;
import com.example.tickets.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@Controller
public class PerformanceController {

    private final PerformanceRepository performanceRepo;
    private final SeatRepository seatRepo;
    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;

    // Добавляем все репозитории в конструктор
    public PerformanceController(PerformanceRepository performanceRepo,
                                 SeatRepository seatRepo,
                                 TicketRepository ticketRepo,
                                 UserRepository userRepo) {
        this.performanceRepo = performanceRepo;
        this.seatRepo = seatRepo;
        this.ticketRepo = ticketRepo;
        this.userRepo = userRepo;
    }

    // Страница афиши — без изменений
    @GetMapping("/performances")
    public String performancesPage(Model model) {
        model.addAttribute("genres", new String[] {
                "комедия","драма","мюзикл","трагедия","балет","детский спектакль","опера"
        });
        return "performances";
    }

    // Детальная страница — без изменений
    @GetMapping("/performances/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Performance p = performanceRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Performance not found: " + id));
        model.addAttribute("p", p);
        return "performance_detail";
    }

    // СТРАНИЦА ВЫБОРА МЕСТ — ИЗМЕНЕНИЯ ЗДЕСЬ
    @GetMapping("/performances/{id}/buy")
    public String buy(@PathVariable Long id, Model model) {
        Performance p = performanceRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Performance not found: " + id));

        // Получаем ВСЕ места для этого спектакля из БД
        List<Seat> seats = seatRepo.findByPerformanceId(id);

        model.addAttribute("p", p);
        model.addAttribute("seats", seats); // ← Это главное изменение!

        return "buy_ticket";
    }

    // JSON API для афиши — без изменений
    @GetMapping("/api/performances")
    @ResponseBody
    public Page<Performance> apiPerformances(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "3") int size,
                                             @RequestParam(defaultValue = "") String genre,
                                             @RequestParam(defaultValue = "") String actor,
                                             @RequestParam(defaultValue = "") String search) {
        PageRequest pr = PageRequest.of(page, size);

        if (genre.isBlank() && search.isBlank() && actor.isBlank()) {
            return performanceRepo.findAll(pr);
        }

// actor only
        if (!actor.isBlank() && genre.isBlank() && search.isBlank()) {
            return performanceRepo.findByActorName(actor, pr);
        }

// actor + search
        if (!actor.isBlank() && genre.isBlank() && !search.isBlank()) {
            return performanceRepo.findByTitleAndActorName(search, actor, pr);
        }

// actor + genre
        if (!actor.isBlank() && !genre.isBlank() && search.isBlank()) {
            return performanceRepo.findByGenreAndActorName(genre, actor, pr);
        }

// actor + genre + search
        if (!actor.isBlank() && !genre.isBlank() && !search.isBlank()) {
            return performanceRepo.findByGenreTitleActor(genre, search, actor, pr);
        }

// старые случаи без actor
        if (!genre.isBlank() && search.isBlank()) return performanceRepo.findByGenreContainingIgnoreCase(genre, pr);
        if (genre.isBlank() && !search.isBlank()) return performanceRepo.findByTitleContainingIgnoreCase(search, pr);
        return performanceRepo.findByGenreContainingIgnoreCaseAndTitleContainingIgnoreCase(genre, search, pr);

    }

    // НОВЫЙ МЕТОД: покупка билетов через AJAX
    @PostMapping("/api/performances/{id}/buy")
    @ResponseBody
    public ResponseEntity<String> purchaseTickets(

            @PathVariable Long id,
            @RequestBody List<Map<String, Object>> selectedSeats, // [{row: "A", num: 1}, ...]
            Authentication authentication) { // Получаем текущего пользователя

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build(); // без тела сообщения
        }

        System.out.println("=== ПОКУПКА БИЛЕТОВ ===");
        System.out.println("Спектакль ID: " + id);
        System.out.println("Пользователь: " + authentication.getName());
        System.out.println("Количество выбранных мест: " + selectedSeats.size());

        for (Map<String, Object> seatData : selectedSeats) {
            System.out.println("Пришло от клиента: " + seatData);
            String row = (String) seatData.get("row");
            Object numObj = seatData.get("num");
            System.out.println("row = '" + row + "', num = " + numObj + " (тип: " + (numObj != null ? numObj.getClass().getSimpleName() : "null") + ")");
        }
        System.out.println("Всего мест в зале для этого спектакля: " + seatRepo.findByPerformanceId(id).size());
        System.out.println("==========================");

        AppUser currentUser = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Performance performance = performanceRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Спектакль не найден"));

        for (Map<String, Object> seatData : selectedSeats) {
            String row = ((String) seatData.get("row")).trim();
            Integer num = (Integer) seatData.get("num");

            Seat seat = seatRepo.findByPerformanceId(id).stream()
                    .filter(s -> s.getRowLabel().equals(row) && s.getSeatNumber() == num)
                    .findFirst()
                    .orElse(null);
            if (seat == null) {
                return ResponseEntity.badRequest().body("Место " + row + num + " не существует");
            }
            if (seat.isOccupied()) {
                return ResponseEntity.badRequest().body("Место " + row + num + " уже занято");
            }

            // Занимаем место
            seat.setOccupied(true);
            seatRepo.save(seat);

            // Создаём билет
            Ticket ticket = new Ticket();
            ticket.setEventName(performance.getTitle());
            ticket.setBuyerName(currentUser.getUsername());
            ticket.setUser(currentUser);
            ticket.setPerformance(performance);
            ticket.setSeat(seat);
            ticketRepo.save(ticket);
        }

        return ResponseEntity.ok("Билеты успешно куплены");
    }

    @PostMapping("/api/performances/{id}/favorite")
    @ResponseBody
    public ResponseEntity<String> addToFavorite(@PathVariable Long id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();

        AppUser user = userRepo.findByUsername(auth.getName()).orElseThrow();
        Performance p = performanceRepo.findById(id).orElseThrow();

        p.getFavoritedBy().add(user);              // достаточно менять владельца
        performanceRepo.save(p);                   // ✅ сохраняем владельца

        return ResponseEntity.ok("Добавлено в избранное");
    }

    @DeleteMapping("/api/performances/{id}/favorite")
    @ResponseBody
    public ResponseEntity<String> removeFromFavorite(@PathVariable Long id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();

        AppUser user = userRepo.findByUsername(auth.getName()).orElseThrow();
        Performance p = performanceRepo.findById(id).orElseThrow();

        p.getFavoritedBy().remove(user);
        performanceRepo.save(p);

        return ResponseEntity.ok("Удалено из избранного");
    }


    // Проверить, в избранном ли
    @GetMapping("/api/performances/{id}/favorite")
    @ResponseBody
    public boolean isFavorite(@PathVariable Long id, Authentication auth) {
        if (auth == null) return false;

        AppUser user = userRepo.findByUsername(auth.getName()).orElse(null);
        if (user == null) return false;

        return user.getFavoritePerformances()
                .stream()
                .anyMatch(p -> p.getId() != null && p.getId().equals(id));
    }


    @GetMapping("/api/user/authenticated")
    @ResponseBody
    public boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated();
    }
}