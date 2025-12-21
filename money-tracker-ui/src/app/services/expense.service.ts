import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Expense {
  id: string;
  monthId: string;
  categoryId: string;
  amount: number;
  description: string;
}

@Injectable({ providedIn: 'root' })
export class ExpenseService {

  private API = 'http://localhost:8080/api/expenses';

  constructor(private http: HttpClient) {}

  getExpensesByMonth(monthId: string) {
    return this.http.get<Expense[]>(`${this.API}/month/${monthId}`);
  }
}
