package com.waynetye.myapp.controller;

import com.waynetye.myapp.model.Expense;
import com.waynetye.myapp.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*") // allow access from frontend
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    // ✅ 1. Get all expenses
    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // ✅ 2. Get expense by ID
    @GetMapping("/{id}")
    public Optional<Expense> getExpenseById(@PathVariable String id) {
        return expenseRepository.findById(id);
    }

    // ✅ 3. Create a new expense
    @PostMapping
    public Expense createExpense(@RequestBody Expense expense) {
        return expenseRepository.save(expense);
    }

    // ✅ 4. Update an expense
    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable String id, @RequestBody Expense expenseDetails) {
        return expenseRepository.findById(id)
                .map(expense -> {
                    expense.setMonthId(expenseDetails.getMonthId());
                    expense.setCategoryId(expenseDetails.getCategoryId());
                    expense.setAmount(expenseDetails.getAmount());
                    expense.setDescription(expenseDetails.getDescription());
                    return expenseRepository.save(expense);
                })
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    // ✅ 5. Delete an expense
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable String id) {
        expenseRepository.deleteById(id);
    }

    // ✅ 6. Get expenses for a specific month
    @GetMapping("/month/{monthId}")
    public List<Expense> getExpensesByMonth(@PathVariable String monthId) {
        return expenseRepository.findByMonthId(monthId);
    }

    // ✅ 7. Get expenses for a specific month and category
    @GetMapping("/month/{monthId}/category/{categoryId}")
    public List<Expense> getExpensesByMonthAndCategory(@PathVariable String monthId, @PathVariable String categoryId) {
        return expenseRepository.findByMonthIdAndCategoryId(monthId, categoryId);
    }

    // ✅ 8. Get all expenses for a category
    @GetMapping("/category/{categoryId}")
    public List<Expense> getExpensesByCategory(@PathVariable String categoryId) {
        return expenseRepository.findByCategoryId(categoryId);
    }
}
