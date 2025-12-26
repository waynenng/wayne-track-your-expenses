import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ExpenseService, Expense } from '../../services/expense.service';
import { CategoryService, Category } from '../../services/category.service';

@Component({
  selector: 'app-month',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './month.html',
})
export class MonthComponent implements OnInit {

  expenses: Expense[] = [];
  categories: Category[] = [];

  monthId!: string;

  // ✅ default currency
  currency: 'RM' | '$' = 'RM';

  // ➕ form fields
  amount!: number;
  description = '';
  date = new Date().toISOString().substring(0, 10);
  categoryId!: string;

  constructor(
    private route: ActivatedRoute,
    private expenseService: ExpenseService,
    private categoryService: CategoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.monthId = this.route.snapshot.paramMap.get('id')!;
    this.loadExpenses();
    this.loadCategories();
  }

  // 📥 Expenses
  loadExpenses() {
    this.expenseService.getExpensesByMonth(this.monthId).subscribe({
      next: (data) => {
        this.expenses = data;
      },
      error: (err) => console.error(err)
    });
  }

  // 📥 Categories
  loadCategories() {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        if (data.length > 0) {
          this.categoryId = data[0].id; // default
        }
      },
      error: err => console.error('Failed to load categories', err)
    });
  }

  // ➕ Add expense
  addExpense() {
    // ✅ basic validation
    if (
      !this.amount ||
      this.amount <= 0 ||
      !this.description?.trim() ||
      !this.categoryId ||
      !this.date
    ) {
      return;
    }

    const expense: Expense = {
      monthId: this.monthId,
      categoryId: this.categoryId,
      amount: Number(this.amount),
      description: this.description.trim(),
      date: this.date
    };

    this.expenseService.addExpense(expense).subscribe({
      next: () => {
        // ✅ reset form cleanly
        this.amount = 0;
        this.description = '';
        this.categoryId = '';
        this.date = '';

        // ✅ reload from backend (important)
        this.loadExpenses();
      },
      error: err => {
        console.error('Failed to add expense', err);
      }
    });
  }

  back() {
    this.router.navigate(['/home']);
  }
}
