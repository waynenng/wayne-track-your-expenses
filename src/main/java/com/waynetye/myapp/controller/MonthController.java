package com.waynetye.myapp.controller;

import com.waynetye.myapp.model.Month;
import com.waynetye.myapp.repository.MonthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/months")
@CrossOrigin(origins = "*")
public class MonthController {

    @Autowired
    private MonthRepository monthRepository;

    /**
     * ✅ Get all months for a user (chronologically ordered)
     * This is what the Home UI will call.
     */
    @GetMapping("/by-user")
    public List<Month> getMonthsByUser(@RequestParam String userId) {
        ensureCurrentMonthExists(userId);
        return monthRepository.findByUserIdOrderByYearAscMonthAsc(userId);
    }

    /**
     * ✅ (Optional) Get a month by ID
     * Useful for expense views
     */
    @GetMapping("/{id}")
    public Month getMonthById(@PathVariable String id) {
        return monthRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Month not found"));
    }

    /**
     * 🔒 INTERNAL SYSTEM LOGIC
     * Ensures the current calendar month exists for the user.
     */
    private void ensureCurrentMonthExists(String userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue(); // 1–12

        boolean exists = monthRepository
                .findByUserIdAndYearAndMonth(userId, year, month)
                .isPresent();

        if (!exists) {
            Month newMonth = new Month(year, month, userId);
            monthRepository.save(newMonth);
        }
    }
}
