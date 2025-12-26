import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';

export const authGuard = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.authState$().pipe(
    take(1), // wait for value
    map(isLoggedIn => {
      return isLoggedIn ? true : router.createUrlTree(['/']);
    })
  );
};
