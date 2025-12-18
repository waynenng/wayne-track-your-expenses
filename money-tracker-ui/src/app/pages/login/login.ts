import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
})
export class LoginComponent {

  email = '';
  password = '';
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLogin() {
    this.error = '';

    if (!this.email || !this.password) {
      this.error = 'Email and password are required';
      return;
    }

    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        // ✅ login success
        this.router.navigate(['/home']);
      },
      error: () => {
        // ❌ login failed
        this.error = 'Invalid email or password';
      }
    });
  }
}
