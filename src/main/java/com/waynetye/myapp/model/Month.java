package com.waynetye.myapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "months")
public class Month {

    @Id
    private String id;

    private int year;        // e.g. 2025
    private int month;       // 1–12
    private String userId;   // owner
    private double total;

    private List<String> expenseIds;

    // Default constructor
    public Month() {}

    public Month(int year, int month, String userId) {
        this.year = year;
        this.month = month;
        this.userId = userId;
    }

    public String getId() { return id; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<String> getExpenseIds() { return expenseIds; }
    public void setExpenseIds(List<String> expenseIds) { this.expenseIds = expenseIds; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}

