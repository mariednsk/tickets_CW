package com.example.tickets.controller;

import com.example.tickets.model.AppUser;
import com.example.tickets.model.Role;
import com.example.tickets.repository.UserRepository;
import com.example.tickets.security.RegistrationDto;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }



    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("user") RegistrationDto dto,
                             BindingResult br, Model model) {
        if (br.hasErrors()) return "register";
        if (!dto.getPassword().equals(dto.getConfirm())) {
            br.rejectValue("confirm", "mismatch", "Пароли не совпадают");
            return "register";
        }
        if (users.existsByUsername(dto.getUsername())) {
            br.rejectValue("username", "exists", "Логин уже занят");
            return "register";
        }
        AppUser u = new AppUser();
        u.setUsername(dto.getUsername());
        u.setPassword(encoder.encode(dto.getPassword()));
        u.getRoles().add(Role.USER);
        users.save(u);
        return "redirect:/login?registered";
    }
}
