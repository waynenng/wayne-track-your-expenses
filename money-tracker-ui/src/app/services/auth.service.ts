import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private API_URL = 'http://localhost:8080/api/users';

  private loggedIn$ = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient) {
    if (typeof window !== 'undefined') {
      const stored = localStorage.getItem('loggedIn') === 'true';
      this.loggedIn$.next(stored);
    }
  }

  login(email: string, password: string) {
    return this.http.post<any>(`${this.API_URL}/login`, { email, password })
      .pipe(
        tap(res => {
          localStorage.setItem('loggedIn', 'true');
          localStorage.setItem('userId', res.userId);
          this.loggedIn$.next(true);
        })
      );
  }

  logout() {
    localStorage.clear();
    this.loggedIn$.next(false);
  }

  // 🔑 THIS is what guards will use
  authState$() {
    return this.loggedIn$.asObservable();
  }

  getUserId(): string | null {
    return localStorage.getItem('userId');
  }
}
