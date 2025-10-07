package com.waynetye.myapp.repository;

import com.waynetye.myapp.model.Month;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonthRepository extends MongoRepository<Month, String> {

    // Find all months for a specific user
    List<Month> findByUserId(String userId);

    // Optional: find a month by name and user (useful for current month lookup)
    Month findByNameAndUserId(String name, String userId);
}
