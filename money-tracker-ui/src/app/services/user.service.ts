import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  id: string;
  firstName?: string;
  lastName?: string;
  email: string;
  currency: 'RM' | '$';
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private API_URL = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  /**
   * Update user's preferred currency
   */
  updateCurrency(userId: string, currency: 'RM' | '$'): Observable<User> {
    return this.http.put<User>(
      `${this.API_URL}/${userId}/currency`,
      { currency }
    );
  }

  /**
   * Fetch user (useful after login / refresh)
   */
  getUserById(userId: string): Observable<User> {
    return this.http.get<User>(
      `${this.API_URL}/${userId}`
    );
  }
}
