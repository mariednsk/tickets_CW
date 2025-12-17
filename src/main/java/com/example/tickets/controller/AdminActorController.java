package com.example.tickets.controller;

import com.example.tickets.model.Actor;
import com.example.tickets.repository.ActorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/admin/actors")
public class AdminActorController {

    private final ActorRepository actorRepo;

    public AdminActorController(ActorRepository actorRepo) {
        this.actorRepo = actorRepo;
    }

    @GetMapping
    public String adminActors(Model model) {
        model.addAttribute("actors", actorRepo.findAll());
        model.addAttribute("actor", new Actor()); // Для формы добавления
        return "admin_actors"; // Этот шаблон должен быть в templates/admin/admin_actors.html
    }

    @PostMapping("/add")
    public String addActor(@ModelAttribute Actor actor, @RequestParam("photoFile") MultipartFile photoFile) throws IOException {
        if (photoFile != null && !photoFile.isEmpty()) {
            actor.setPhoto(saveActorPhoto(photoFile)); // actors/uuid_name.jpg
        }
        actorRepo.save(actor);
        return "redirect:/admin/actors";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        Actor actor = actorRepo.findById(id).orElseThrow();
        model.addAttribute("actor", actor);
        return "admin_actor_edit";
    }

    @PostMapping("/edit/{id}")
    public String saveEdit(@PathVariable Long id, @ModelAttribute Actor form, @RequestParam("photoFile") MultipartFile photoFile) throws IOException {
        Actor actor = actorRepo.findById(id).orElseThrow();
        actor.setFullName(form.getFullName());
        actor.setBio(form.getBio());
        if (photoFile != null && !photoFile.isEmpty()) {
            actor.setPhoto(saveActorPhoto(photoFile));
        }
        actorRepo.save(actor);
        return "redirect:/admin/actors";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        actorRepo.deleteById(id);
        return "redirect:/admin/actors";
    }

    private String saveActorPhoto(MultipartFile file) throws IOException {
        Path dir = Paths.get("uploads", "actors");
        Files.createDirectories(dir);

        String original = file.getOriginalFilename() == null ? "photo" : file.getOriginalFilename();
        String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String newName = UUID.randomUUID() + "_" + safeName;

        Path target = dir.resolve(newName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "actors/" + newName; // Сохраняем в БД
    }
}
