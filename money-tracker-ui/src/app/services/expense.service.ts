import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Expense {
  id?: string;          // 🔒 optional (backend generates)
  monthId: string;
  categoryId: string;
  amount: number;
  description: string;
  date: string;
}

@Injectable({ providedIn: 'root' })
export class ExpenseService {

  private API = 'http://localhost:8080/api/expenses';

  constructor(private http: HttpClient) {}

  // 📥 Get expenses for a month
  getExpensesByMonth(monthId: string): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.API}/month/${monthId}`);
  }

  // ➕ Add a new expense
  addExpense(expense: Expense): Observable<Expense> {
    return this.http.post<Expense>(this.API, expense);
  }
}
