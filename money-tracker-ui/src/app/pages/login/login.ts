import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {

  email = '';
  password = '';

  constructor(private router: Router) {}

  login() {
    // TEMP: just navigate to home for now
    console.log(this.email, this.password);
    this.router.navigate(['/home']);
  }
}
