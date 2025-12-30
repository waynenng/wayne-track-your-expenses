import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { ExpenseService, Expense } from '../../services/expense.service';
import { CategoryService, Category } from '../../services/category.service';
import { MonthService, Month } from '../../services/month.service';
import { AuthService } from '../../services/auth.service';

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
  monthYear!: number;
  monthNumber!: number;

  currency: 'RM' | '$' = 'RM';

  amount!: number;
  description = '';
  date = new Date().toISOString().substring(0, 10);
  categoryId!: string;

  constructor(
    private route: ActivatedRoute,
    private expenseService: ExpenseService,
    private categoryService: CategoryService,
    private router: Router,
    private authService: AuthService,
    private monthService: MonthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.monthId = this.route.snapshot.paramMap.get('id')!;
    this.loadMonthMeta();
    this.loadExpenses();
    this.loadCategories();
  }

  loadMonthMeta() {
    this.monthService.getMonthById(this.monthId).subscribe({
      next: (month: Month) => {
        this.monthYear = month.year;
        this.monthNumber = month.month;
      },
      error: (err: any) => console.error('Failed to load month metadata', err)
    });
  }

  loadExpenses() {
    this.expenseService.getExpensesByMonth(this.monthId).subscribe({
      next: (data) => {
        this.expenses = [...data].sort(
          (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
        );

        this.cdr.markForCheck(); // 🔥 FORCE UI UPDATE
      },
      error: (err) => console.error(err)
    });
  }

  getMonthlyTotal(): number {
    return this.expenses.reduce((sum, e) => sum + e.amount, 0);
  }

  getCategoryName(categoryId: string): string {
    const category = this.categories.find(c => c.id === categoryId);
    return category ? category.name : '—';
  }

  loadCategories() {
    const userId = this.authService.getUserId();
    if (!userId) return;

    this.categoryService.getCategories(userId).subscribe({
      next: (data) => {
        this.categories = data;
        if (data.length > 0) {
          this.categoryId = data[0].id;
        }
      },
      error: err => console.error('Failed to load categories', err)
    });
  }

  addExpense() {
    if (
      !this.amount ||
      this.amount <= 0 ||
      !this.description.trim() ||
      !this.categoryId ||
      !this.date
    ) {
      return;
    }

    // 🔒 DATE VALIDATION (frontend)
    const selectedDate = new Date(this.date);
    const selectedYear = selectedDate.getFullYear();
    const selectedMonth = selectedDate.getMonth() + 1; // JS = 0-based

    if (
      selectedYear !== this.monthYear ||
      selectedMonth !== this.monthNumber
    ) {
      alert('You can only add expenses within this month.');
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
      next: (savedExpense) => {

        // ✅ instant UI update + stable ordering
        this.expenses = [
          savedExpense,
          ...this.expenses
        ].sort((a, b) =>
          new Date(b.date).getTime() - new Date(a.date).getTime()
        );

        // 🔥 required because of SSR + standalone components
        this.cdr.markForCheck();

        // ♻️ reset form
        this.amount = 0;
        this.description = '';
        this.date = new Date().toISOString().substring(0, 10);
      },
      error: err => console.error(err)
    });
  }

  back() {
    this.router.navigate(['/home']);
  }
}
