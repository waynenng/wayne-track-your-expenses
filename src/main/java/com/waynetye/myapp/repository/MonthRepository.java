package com.waynetye.myapp.repository;

import com.waynetye.myapp.model.Month;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthRepository extends MongoRepository<Month, String> {

    // ✅ Get all months for a user, ordered chronologically
    List<Month> findByUserIdOrderByYearDescMonthDesc(String userId);

    // ✅ Find a specific month deterministically
    Optional<Month> findByUserIdAndYearAndMonth(
            String userId,
            int year,
            int month
    );
}
