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
import java.util.Map;
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
    private MonthRepository monthRepository; // inject Month repository

    @Autowired
    private PasswordEncoder passwordEncoder; // inject BCrypt encoder

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id);
    }

    // Create a new user (generic)
    @PostMapping
    public User createUser(@RequestBody User user) {
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Update user
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

    // Delete user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
    }

    // Register user (email must be unique + create current month)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email already registered");
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // Do NOT create month here
        // MonthController will ensure current month exists

        return ResponseEntity.ok("User registered successfully");
    }

    // Login user (BCrypt password comparison)
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginData) {
        Optional<User> existingUser = userRepository.findByEmail(loginData.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {

                // Defensive default
                String currency = user.getCurrency() != null ? user.getCurrency() : "RM";

                // Optional: persist fix back to DB
                if (user.getCurrency() == null) {
                    user.setCurrency("RM");
                    userRepository.save(user);
                }

                return ResponseEntity.ok(
                        Map.of(
                                "userId", user.getId(),
                                "currency", currency,
                                "message", "Login successful"
                        )
                );
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }

    @PutMapping("/{id}/currency")
    public ResponseEntity<?> updateCurrency(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) {
        String currency = body.get("currency");

        if (!"RM".equals(currency) && !"$".equals(currency)) {
            return ResponseEntity.badRequest().body("Invalid currency");
        }

        return userRepository.findById(id)
                .map(user -> {
                    user.setCurrency(currency);
                    userRepository.save(user);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
