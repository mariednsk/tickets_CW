package com.example.tickets.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin_dashboard"; // Этот шаблон должен быть в templates/admin/admin_dashboard.html
    }

    // Прочие маппинги для управления спектаклями и билетами
}
