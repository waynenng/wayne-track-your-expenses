import { Component, OnInit, NgZone, ChangeDetectorRef, ApplicationRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { UserService } from '../../services/user.service';
import { ExpenseService, Expense } from '../../services/expense.service';
import { CategoryService, Category } from '../../services/category.service';
import { MonthService, Month } from '../../services/month.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-month',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './month.html',
  styleUrls: ['./month.css']
})
export class MonthComponent implements OnInit {

  expenses: Expense[] = [];
  categories: Category[] = [];

  monthId!: string;
  monthYear!: number;
  monthNumber!: number;

  showNewCategory = false;
  newCategoryName = '';
  categoryError = '';
  currency: 'RM' | '$' = 'RM';

  amount!: number;
  description = '';
  date = new Date().toISOString().substring(0, 10);
  categoryId!: string;

  selectedCategoryId: string = 'ALL';
  filteredExpenses: Expense[] = [];

  editingExpenseId: string | null = null;

  editForm = {
    amount: 0,
    description: '',
    date: '',
    categoryId: ''
  };

  constructor(
    private route: ActivatedRoute,
    private expenseService: ExpenseService,
    private categoryService: CategoryService,
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private monthService: MonthService,
    private zone: NgZone,
    private appRef: ApplicationRef,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.monthId = this.route.snapshot.paramMap.get('id')!;
    this.loadMonthMeta();
    this.currency = this.authService.getCurrency();
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

  onCurrencyChange(currency: 'RM' | '$') {
    this.currency = currency;
    this.authService.setCurrency(currency);

    const userId = this.authService.getUserId();
    if (userId) {
      this.userService.updateCurrency(userId, currency).subscribe();
    }
  }

  loadExpenses() {
    this.expenseService.getExpensesByMonth(this.monthId).subscribe({
      next: (data) => {
        // 1️⃣ Store ALL expenses (sorted)
        this.expenses = [...data].sort(
          (a, b) =>
            new Date(b.date).getTime() - new Date(a.date).getTime()
        );

        // 2️⃣ Apply category filter AFTER loading
        this.applyCategoryFilter();

        // 3️⃣ Force UI update (important)
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  applyCategoryFilter() {
    if (this.selectedCategoryId === 'ALL') {
      this.filteredExpenses = [...this.expenses];
    } else {
      this.filteredExpenses = this.expenses.filter(
        e => e.categoryId === this.selectedCategoryId
      );
    }
  }

  getMonthlyTotal(): number {
    return this.filteredExpenses.reduce((sum, e) => sum + e.amount, 0);
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
      this.categoryId === '__new__' ||
      !this.date
    ) {
      return;
    }

    // DATE VALIDATION (frontend)
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

        // Update source-of-truth list (NEWEST FIRST)
        this.expenses = [
          savedExpense,
          ...this.expenses
        ].sort((a, b) =>
          new Date(b.date).getTime() - new Date(a.date).getTime()
        );

        // Re-apply category filter
        this.applyCategoryFilter();

        // Force immediate UI update
        this.cdr.detectChanges();

        // Reset form (safe defaults)
        this.amount = 0;
        this.description = '';
        this.date = new Date().toISOString().substring(0, 10);
      },
      error: err => console.error(err)
    });
  }


  addCategory() {
    if (!this.newCategoryName?.trim()) return;

    const exists = this.categories.some(
      c => c.name.toLowerCase() === this.newCategoryName.trim().toLowerCase()
    );

    if (exists) {
      this.categoryError = 'Category already exists';
      return; // 👈 OK now
    }

    this.categoryError = ''; // clear previous error

    const category = {
      name: this.newCategoryName.trim(),
      userId: this.authService.getUserId()!
    };

    this.categoryService.addCategory(category).subscribe({
      next: (created: Category) => {
        this.categories.push(created);
        this.cdr.detectChanges();
        this.categoryId = created.id;
        this.newCategoryName = '';
        this.showNewCategory = false;
        this.categoryError = '';
      },
      error: (err: any) => {
        this.categoryError = err?.error || 'Failed to add category';
      }
    });

  }

  startEdit(expense: Expense) {
    if (!expense.id) return;   // safety guard
    this.editingExpenseId = expense.id;

    this.editForm = {
      amount: expense.amount,
      description: expense.description,
      date: expense.date,
      categoryId: expense.categoryId
    };
  }

  cancelEdit() {
    this.editingExpenseId = null;
  }

  saveEdit(expense: Expense) {
    if (!expense.id) return;

    const updated: Expense = {
      ...expense,
      amount: this.editForm.amount,
      description: this.editForm.description,
      date: this.editForm.date,
      categoryId: this.editForm.categoryId
    };

    this.expenseService.updateExpense(expense.id, updated).subscribe({
      next: (saved: Expense) => {

        // update master list
        this.expenses = this.expenses.map(e =>
          e.id === saved.id ? saved : e
        );

        // re-sort master list
        this.expenses.sort((a, b) =>
          new Date(b.date).getTime() - new Date(a.date).getTime()
        );

        // regenerate what the UI uses
        this.applyCategoryFilter();

        // exit edit mode
        this.editingExpenseId = null;
      },
      error: err => console.error('Failed to update expense', err)
    });
  }

  deleteExpense(expenseId: string) {
    if (!confirm('Delete this expense?')) return;

    this.expenseService.deleteExpense(expenseId).subscribe({
      next: () => {
        this.zone.run(() => {
          this.expenses = this.expenses.filter(e => e.id !== expenseId);
          this.editingExpenseId = null;

          // force repaint
          this.applyCategoryFilter();
          this.cdr.detectChanges();
        });
      },
      error: err => console.error('Failed to delete expense', err)
    });
  }

  back() {
    this.router.navigate(['/home']);
  }
}
