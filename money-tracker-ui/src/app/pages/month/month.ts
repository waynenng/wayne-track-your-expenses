import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ExpenseService, Expense } from '../../services/expense.service';

@Component({
  selector: 'app-month',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './month.html',
})
export class MonthComponent implements OnInit {

  expenses: Expense[] = [];
  monthId!: string;

  // ✅ default currency
  currency: 'RM' | '$' = 'RM';

  constructor(
    private route: ActivatedRoute,
    private expenseService: ExpenseService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.monthId = this.route.snapshot.paramMap.get('id')!;
    this.loadExpenses();
  }

  loadExpenses() {
    this.expenseService.getExpensesByMonth(this.monthId).subscribe({
      next: (data) => {
        this.expenses = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  back() {
    this.router.navigate(['/home']);
  }
}
