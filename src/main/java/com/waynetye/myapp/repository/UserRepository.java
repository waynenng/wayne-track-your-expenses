package com.waynetye.myapp.repository;

import com.waynetye.myapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Find a user by their email (for login)
    User findByEmail(String email);

    // Optional: find a user by username if you decide to keep it
    // User findByUsername(String username);
}
