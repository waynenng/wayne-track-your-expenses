import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { HomeComponent } from './pages/home/home';
import { authGuard } from './guards/auth-guard';
import { RegisterComponent } from './pages/register/register';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  {
    path: 'month/:id',
    loadComponent: () =>
      import('./pages/month/month').then(m => m.MonthComponent),
      canActivate: [authGuard]
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register').then(m => m.RegisterComponent)
  }
];
