import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Month {
  id: string;
  year: number;
  month: number; // 🔴 THIS MUST EXIST
  userId: string;
}

@Injectable({
  providedIn: 'root'
})
export class MonthService {

  private baseUrl = 'http://localhost:8080/api/months';

  constructor(private http: HttpClient) {}

  getMonthsByUser(userId: string): Observable<Month[]> {
    return this.http.get<Month[]>(
      `${this.baseUrl}/by-user?userId=${userId}`
    );
  }
}
