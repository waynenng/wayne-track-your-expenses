import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-month',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './month.html',
  styleUrl: './month.css',
})
export class MonthComponent implements OnInit {

  monthId!: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.monthId = this.route.snapshot.paramMap.get('id')!;

    console.log('Opened month:', this.monthId);
  }

  back() {
    this.router.navigate(['/home']);
  }
}
