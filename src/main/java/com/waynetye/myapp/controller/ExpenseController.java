package com.waynetye.myapp.controller;

import com.waynetye.myapp.model.Expense;
import com.waynetye.myapp.model.Month;
import com.waynetye.myapp.repository.ExpenseRepository;
import com.waynetye.myapp.repository.MonthRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private MonthRepository monthRepository;

    // Get all expenses
    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // Get expense by ID
    @GetMapping("/{id}")
    public Optional<Expense> getExpenseById(@PathVariable String id) {
        return expenseRepository.findById(id);
    }

    // Create a new expense (WITH DATE VALIDATION)
    @PostMapping
    public Expense createExpense(@RequestBody Expense expense) {

        // Default date if not provided
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }

        // 🔒 Load the month this expense belongs to
        Month month = monthRepository.findById(expense.getMonthId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Month not found"
                ));

        LocalDate expenseDate = expense.getDate();

        // Block expenses outside the month
        if (
                expenseDate.getYear() != month.getYear() ||
                        expenseDate.getMonthValue() != month.getMonth()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expense date must be within the selected month"
            );
        }

        return expenseRepository.save(expense);
    }

    // Update an expense
    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable String id, @RequestBody Expense expenseDetails) {
        return expenseRepository.findById(id)
                .map(expense -> {
                    expense.setMonthId(expenseDetails.getMonthId());
                    expense.setCategoryId(expenseDetails.getCategoryId());
                    expense.setAmount(expenseDetails.getAmount());
                    expense.setDescription(expenseDetails.getDescription());
                    expense.setDate(expenseDetails.getDate());
                    return expenseRepository.save(expense);
                })
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    // Delete an expense
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable String id) {
        expenseRepository.deleteById(id);
    }

    // Get expenses for a specific month
    @GetMapping("/month/{monthId}")
    public List<Expense> getExpensesByMonth(@PathVariable String monthId) {
        return expenseRepository.findByMonthId(monthId);
    }

    // Get expenses for a specific month and category
    @GetMapping("/month/{monthId}/category/{categoryId}")
    public List<Expense> getExpensesByMonthAndCategory(
            @PathVariable String monthId,
            @PathVariable String categoryId
    ) {
        return expenseRepository.findByMonthIdAndCategoryId(monthId, categoryId);
    }

    // Get all expenses for a category
    @GetMapping("/category/{categoryId}")
    public List<Expense> getExpensesByCategory(@PathVariable String categoryId) {
        return expenseRepository.findByCategoryId(categoryId);
    }
}
