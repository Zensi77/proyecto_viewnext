import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const isAuthenticatedGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return new Promise((resolve) => {
    const checkUser = () => {
      const isLoading = authService.loading();
      const isLogged = authService.userLogged();

      if (isLoading) {
        setTimeout(checkUser, 100);
      } else if (isLogged) {
        resolve(true);
      } else {
        router.navigate(['/']);
        resolve(false);
      }
    };
    checkUser();
  });
};
