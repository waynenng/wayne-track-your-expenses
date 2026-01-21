package com.waynetye.myapp.controller;

import com.waynetye.myapp.model.Category;
import com.waynetye.myapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // Get categories for a specific user ONLY
    @GetMapping
    public List<Category> getCategoriesByUser(@RequestParam String userId) {
        return categoryRepository.findByUserId(userId);
    }

    // Get category by ID (ownership enforced)
    @GetMapping("/{id}")
    public Category getCategoryById(
            @PathVariable String id,
            @RequestParam String userId
    ) {
        return categoryRepository.findById(id)
                .filter(c -> c.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // Create category (must include userId)
    @PostMapping
    public Category createCategory(@RequestBody Category category) {

        // Normalize name (prevents "Food" vs " food ")
        String normalizedName = category.getName().trim();

        Category existing = categoryRepository
                .findByNameAndUserId(normalizedName, category.getUserId());

        if (existing != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category already exists"
            );
        }

        category.setName(normalizedName);
        return categoryRepository.save(category);
    }

    // Update category (ownership enforced)
    @PutMapping("/{id}")
    public Category updateCategory(
            @PathVariable String id,
            @RequestParam String userId,
            @RequestBody Category categoryDetails
    ) {
        return categoryRepository.findById(id)
                .filter(c -> c.getUserId().equals(userId))
                .map(category -> {
                    category.setName(categoryDetails.getName());
                    category.setDescription(categoryDetails.getDescription());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // Delete category (ownership enforced)
    @DeleteMapping("/{id}")
    public void deleteCategory(
            @PathVariable String id,
            @RequestParam String userId
    ) {
        Category category = categoryRepository.findById(id)
                .filter(c -> c.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(category);
    }
}
