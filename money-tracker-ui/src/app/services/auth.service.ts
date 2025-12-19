import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private API_URL = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  // 🔐 LOGIN
  login(email: string, password: string): Observable<any> {
    return this.http.post(
      `${this.API_URL}/login`,
      { email, password },
      { responseType: 'text' }
    ).pipe(
      tap(() => {
        // ✅ persist login state
        localStorage.setItem('loggedIn', 'true');
      })
    );
  }

  // 🔓 LOGOUT
  logout() {
    localStorage.removeItem('loggedIn');
  }

  // ❓ CHECK LOGIN STATUS
  isLoggedIn(): boolean {
    return localStorage.getItem('loggedIn') === 'true';
  }
}
