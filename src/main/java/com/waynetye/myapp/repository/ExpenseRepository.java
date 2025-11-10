package com.waynetye.myapp.repository;

import com.waynetye.myapp.model.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

    // Find all expenses belonging to a specific month
    List<Expense> findByMonthId(String monthId);

    // Find expenses by month and category
    List<Expense> findByMonthIdAndCategoryId(String monthId, String categoryId);

    // Find all expenses belonging to a specific category
    List<Expense> findByCategoryId(String categoryId);
}
