package com.waynetye.myapp.repository;

import com.waynetye.myapp.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    // Find all categories for a specific user (if user-specific)
    List<Category> findByUserId(String userId);

    // Optional: find a category by name
    Category findByName(String name);

    // Optional: find a category by name and user (for user-specific categories)
    Category findByNameAndUserId(String name, String userId);
}
