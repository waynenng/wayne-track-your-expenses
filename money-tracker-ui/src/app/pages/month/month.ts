import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { ExpenseService, Expense } from '../../services/expense.service';
import { CategoryService, Category } from '../../services/category.service';

@Component({
  selector: 'app-month',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './month.html',
})
export class MonthComponent implements OnInit {

  expenses$!: Observable<Expense[]>;
  categories: Category[] = [];

  monthId!: string;

  currency: 'RM' | '$' = 'RM';

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

  // ✅ reactive expenses
  loadExpenses() {
    this.expenses$ = this.expenseService.getExpensesByMonth(this.monthId);
  }

  getCategoryName(categoryId: string): string {
    const category = this.categories.find(c => c.id === categoryId);
    return category ? category.name : '—';
  }

  loadCategories() {
    this.categoryService.getCategories().subscribe({
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
    ) return;

    const expense: Expense = {
      monthId: this.monthId,
      categoryId: this.categoryId,
      amount: Number(this.amount),
      description: this.description.trim(),
      date: this.date
    };

    this.expenseService.addExpense(expense).subscribe({
      next: () => {
        this.amount = 0;
        this.description = '';
        this.date = new Date().toISOString().substring(0, 10);

        // 🔥 this now refreshes instantly
        this.loadExpenses();
      },
      error: err => console.error('Failed to add expense', err)
    });
  }

  back() {
    this.router.navigate(['/home']);
  }
}
