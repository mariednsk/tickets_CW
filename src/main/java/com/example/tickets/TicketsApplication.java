package com.example.tickets;

import com.example.tickets.model.AppUser;
import com.example.tickets.model.Role;
import com.example.tickets.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TicketsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
        System.out.println("🚀 Приложение запущено! Перейди по адресу http://localhost:8080");
    }

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Администратор создан: admin / admin");
            } else {
                System.out.println("Администратор уже существует.");
            }
        };
    }


}




//package com.example.tickets;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//
//import java.time.LocalDate;
//
//@SpringBootApplication
//public class TicketsApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(TicketsApplication.class, args);
//        System.out.println("Приложение запущено! Перейди по адресу http://localhost:8080");
//    }
//}