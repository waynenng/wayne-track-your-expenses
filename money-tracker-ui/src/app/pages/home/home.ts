import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { MonthService, Month } from '../../services/month.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.html',
})
export class HomeComponent implements OnInit {

  months: Month[] = [];
  userId!: string;

  constructor(
    private authService: AuthService,
    private monthService: MonthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();

    if (!userId) {
      this.router.navigate(['/']);
      return;
    }

    this.userId = userId;
    this.loadMonths();
  }

  loadMonths() {
    this.monthService.getMonthsByUser(this.userId).subscribe({
      next: (data: Month[]) => {
        this.months = data;
        this.cdr.detectChanges(); // 🔥 THIS fixes disappearing UI
      },
      error: (err) => console.error(err)
    });
  }

  getMonthName(month: number): string {
    const names = [
      'January', 'February', 'March', 'April',
      'May', 'June', 'July', 'August',
      'September', 'October', 'November', 'December'
    ];
    return names[month - 1];
  }

  openMonth(month: Month) {
    this.router.navigate(['/month', month.id]);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
