//package com.example.tickets.config;
//
//import com.example.tickets.model.Seat;
//import com.example.tickets.repository.SeatRepository;
//import jakarta.annotation.PostConstruct;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataInit {
//
//    private final SeatRepository repo;
//
//    public DataInit(SeatRepository repo) {
//        this.repo = repo;
//    }
//
//    @PostConstruct
//    public void initSeats() {
//        if (repo.count() > 0) return;
//
//        for (int row = 1; row <= 10; row++) {
//            for (int seat = 1; seat <= 12; seat++) {
//                Seat s = new Seat();
//                s.setRowNumber(row);
//                s.setSeatNumber(seat);
//                repo.save(s);
//            }
//        }
//    }
//}
