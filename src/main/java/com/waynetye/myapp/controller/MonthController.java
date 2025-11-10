package com.waynetye.myapp.controller;

import com.waynetye.myapp.model.Month;
import com.waynetye.myapp.repository.MonthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/months")
@CrossOrigin(origins = "*") // allow access from frontend
public class MonthController {

    @Autowired
    private MonthRepository monthRepository;

    // ✅ 1. Get all months
    @GetMapping
    public List<Month> getAllMonths() {
        return monthRepository.findAll();
    }

    // ✅ 2. Get month by ID
    @GetMapping("/{id}")
    public Optional<Month> getMonthById(@PathVariable String id) {
        return monthRepository.findById(id);
    }

    // ✅ 3. Create a new month
    @PostMapping
    public Month createMonth(@RequestBody Month month) {
        return monthRepository.save(month);
    }

    // ✅ 4. Update month
    @PutMapping("/{id}")
    public Month updateMonth(@PathVariable String id, @RequestBody Month monthDetails) {
        return monthRepository.findById(id)
                .map(month -> {
                    month.setName(monthDetails.getName());
                    month.setYear(monthDetails.getYear());
                    // Optional: add other updates like linked expenses if needed
                    return monthRepository.save(month);
                })
                .orElseThrow(() -> new RuntimeException("Month not found"));
    }

    // ✅ 5. Delete month
    @DeleteMapping("/{id}")
    public void deleteMonth(@PathVariable String id) {
        monthRepository.deleteById(id);
    }
}
