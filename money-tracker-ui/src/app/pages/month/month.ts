import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ExpenseService, Expense } from '../../services/expense.service';

@Component({
  selector: 'app-month',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './month.html',
})
export class MonthComponent implements OnInit {

  expenses: Expense[] = [];
  monthId!: string;

  constructor(
    private route: ActivatedRoute,
    private expenseService: ExpenseService,
    private router: Router,
    private cdr: ChangeDetectorRef   // 👈 IMPORTANT
  ) {}

  ngOnInit(): void {
    this.monthId = this.route.snapshot.paramMap.get('id')!;
    this.loadExpenses();
  }

  loadExpenses() {
    this.expenseService.getExpensesByMonth(this.monthId).subscribe({
      next: (data) => {
        console.log('Expenses from backend:', data); // 🔍 MUST log
        this.expenses = data;
        this.cdr.detectChanges(); // 🔥 FORCE render
      },
      error: (err) => console.error(err)
    });
  }

  back() {
    this.router.navigate(['/home']);
  }
}
