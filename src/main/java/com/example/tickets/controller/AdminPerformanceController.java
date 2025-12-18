package com.example.tickets.controller;

import com.example.tickets.model.Actor;
import com.example.tickets.model.Performance;
import com.example.tickets.repository.ActorRepository;
import com.example.tickets.repository.PerformanceRepository;
import com.example.tickets.repository.SeatRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.tickets.model.Seat;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Controller
@RequestMapping("/admin/performances")
public class AdminPerformanceController {

    private final PerformanceRepository performanceRepo;
    private final ActorRepository actorRepo;
    private final SeatRepository seatRepo;

    public AdminPerformanceController(PerformanceRepository performanceRepo,
                                      ActorRepository actorRepo,
                                      SeatRepository seatRepo) {
        this.performanceRepo = performanceRepo;
        this.actorRepo = actorRepo;
        this.seatRepo = seatRepo;
    }


    @GetMapping
    public String page(@RequestParam(required = false) String search, Model model) {
        List<Performance> performances;
        if (search != null && !search.isEmpty()) {
            performances = performanceRepo.findByTitleContainingIgnoreCase(search);
        } else {
            performances = performanceRepo.findAll();
        }
        model.addAttribute("performances", performances);
        model.addAttribute("actors", actorRepo.findAll());
        model.addAttribute("p", new Performance());
        model.addAttribute("search", search); // чтобы сохранить значение в поле поиска
        return "admin_performances";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("p") Performance p,
                      @RequestParam(value = "actorIds", required = false) List<Long> actorIds,
                      @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            p.setImage(savePerformanceImage(imageFile)); // performances/uuid_name.jpg
        }

        // привязка актёров
        Set<Actor> set = new HashSet<>();
        if (actorIds != null) {
            set.addAll(actorRepo.findAllById(actorIds));
        }


        p.setActors(set);
        Performance saved = performanceRepo.save(p);
        generateSeats(saved);
        return "redirect:/admin/performances";


    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        Performance p = performanceRepo.findById(id).orElseThrow();
        model.addAttribute("p", p);
        model.addAttribute("actors", actorRepo.findAll());
        return "admin_performance_edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute Performance form,
                       @RequestParam(value = "actorIds", required = false) List<Long> actorIds,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Performance p = performanceRepo.findById(id).orElseThrow();

        p.setTitle(form.getTitle());
        p.setDescription(form.getDescription());
        p.setGenre(form.getGenre());
        p.setDate(form.getDate());
        p.setTime(form.getTime());
        p.setPrice(form.getPrice());

        if (imageFile != null && !imageFile.isEmpty()) {
            p.setImage(savePerformanceImage(imageFile));
        }

        Set<Actor> set = new HashSet<>();
        if (actorIds != null) {
            set.addAll(actorRepo.findAllById(actorIds));
        }
        p.setActors(set);

        performanceRepo.save(p);
        return "redirect:/admin/performances";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        seatRepo.deleteByPerformanceId(id);
        performanceRepo.deleteById(id);
        return "redirect:/admin/performances";
    }


    private String savePerformanceImage(MultipartFile file) throws IOException {
        Path dir = Paths.get("uploads", "performances");
        Files.createDirectories(dir);

        String original = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
        String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String newName = UUID.randomUUID() + "_" + safeName;

        Path target = dir.resolve(newName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "performances/" + newName; // хранится в БД
    }

    private void generateSeats(Performance performance) {
        String[] rows = {"A","B","C","D","E","F","G","H","I","J"};
        int seatsPerRow = 12;
        int aisleStart = 5;  // 5-8 проход
        int aisleEnd = 8;

        for (String row : rows) {
            for (int num = 1; num <= seatsPerRow; num++) {
                if (num >= aisleStart && num <= aisleEnd) continue;

                Seat seat = new Seat();
                seat.setRowLabel(row);
                seat.setSeatNumber(num);
                seat.setOccupied(false);
                seat.setPerformance(performance);

                seatRepo.save(seat);
            }
        }
    }

}
