package com.waynetye.myapp.controller;

import com.waynetye.myapp.model.User;
import com.waynetye.myapp.model.Month;
import com.waynetye.myapp.repository.UserRepository;
import com.waynetye.myapp.repository.MonthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // allows access from frontend apps
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MonthRepository monthRepository; // ✅ inject Month repository

    @Autowired
    private PasswordEncoder passwordEncoder; // ✅ inject BCrypt encoder

    // ✅ 1. Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ 2. Get user by ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id);
    }

    // ✅ 3. Create a new user (generic)
    @PostMapping
    public User createUser(@RequestBody User user) {
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // ✅ 4. Update user
    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(userDetails.getFirstName());
                    user.setLastName(userDetails.getLastName());
                    user.setEmail(userDetails.getEmail());
                    // Encrypt new password before saving
                    user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ 5. Delete user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
    }

    // ✅ 6. Register user (email must be unique + create current month)
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Error: Email already registered!";
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // Automatically create current month for the new user
        LocalDate today = LocalDate.now();
        String monthName = today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = today.getYear();

        Month firstMonth = new Month(monthName, year, savedUser.getId());
        monthRepository.save(firstMonth);

        return "User registered successfully with current month created!";
    }

    // ✅ 7. Login user (BCrypt password comparison)
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginData) {
        Optional<User> existingUser = userRepository.findByEmail(loginData.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
                return ResponseEntity.ok("Login successful");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }
}
