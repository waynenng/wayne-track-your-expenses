package com.waynetye.myapp.controller;

import com.waynetye.myapp.model.User;
import com.waynetye.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // allows access from frontend apps
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
                    user.setPassword(userDetails.getPassword());
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
        userRepository.save(user);
        return "User registered successfully!";
    }

    // ✅ 7. Login user
    @PostMapping("/login")
    public String loginUser(@RequestBody User loginData) {
        return userRepository.findByEmail(loginData.getEmail())
                .filter(user -> user.getPassword().equals(loginData.getPassword()))
                .map(user -> "Login successful!")
                .orElse("Error: Invalid email or password!");
    }
}
