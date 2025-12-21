import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private API_URL = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  login(email: string, password: string) {
    return this.http.post<any>(
      `${this.API_URL}/login`,
      { email, password }
    );
  }

  logout() {
    if (typeof window !== 'undefined') {
      localStorage.clear();
    }
  }

  isLoggedIn(): boolean {
    if (typeof window === 'undefined') return false;
    return localStorage.getItem('loggedIn') === 'true';
  }

  setUser(userId: string) {
    if (typeof window !== 'undefined') {
      localStorage.setItem('loggedIn', 'true');
      localStorage.setItem('userId', userId);
    }
  }

  getUserId(): string | null {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem('userId');
  }
}
