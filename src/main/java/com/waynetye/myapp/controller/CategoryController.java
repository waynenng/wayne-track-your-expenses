package com.waynetye.myapp.controller;

import com.waynetye.myapp.model.Category;
import com.waynetye.myapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // ✅ 1. Get categories for a specific user ONLY
    @GetMapping
    public List<Category> getCategoriesByUser(@RequestParam String userId) {
        return categoryRepository.findByUserId(userId);
    }

    // ✅ 2. Get category by ID (ownership enforced)
    @GetMapping("/{id}")
    public Category getCategoryById(
            @PathVariable String id,
            @RequestParam String userId
    ) {
        return categoryRepository.findById(id)
                .filter(c -> c.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // ✅ 3. Create category (must include userId)
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        if (category.getUserId() == null) {
            throw new RuntimeException("userId is required");
        }
        return categoryRepository.save(category);
    }

    // ✅ 4. Update category (ownership enforced)
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

    // ✅ 5. Delete category (ownership enforced)
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
