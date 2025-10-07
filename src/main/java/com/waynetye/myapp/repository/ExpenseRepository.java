package com.waynetye.myapp.repository;

import com.waynetye.myapp.model.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

    // Find all expenses belonging to a specific month
    List<Expense> findByMonthId(String monthId);

    // Optional: find expenses by category within a month
    List<Expense> findByMonthIdAndCategory(String monthId, String category);
}
