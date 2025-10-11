package com.waynetye.myapp.controller;

import com.waynetye.myapp.model.User;
import com.waynetye.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // allows access from frontend apps
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

    // ✅ 6. Register user (email must be unique)
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Error: Email already registered!";
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "User registered successfully!";
    }

    // ✅ 7. Login user (BCrypt password comparison)
    @PostMapping("/login")
    public String loginUser(@RequestBody User loginData) {
        Optional<User> existingUser = userRepository.findByEmail(loginData.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Compare raw password with hashed password
            if (passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
                return "Login successful!";
            } else {
                return "Error: Invalid email or password!";
            }
        } else {
            return "Error: Invalid email or password!";
        }
    }
}
