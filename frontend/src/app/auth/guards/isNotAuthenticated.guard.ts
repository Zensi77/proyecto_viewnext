import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const isNotAuthenticatedGuard: CanActivateFn = () => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return new Promise((resolve) => {
    const checkUser = () => {
      const isLoading = authService.loading();
      const user = authService.user();

      if (isLoading) {
        setTimeout(checkUser, 100);
      } else if (user) {
        router.navigateByUrl('/');
        resolve(false);
      } else {
        resolve(true);
      }
    };
    checkUser();
  });
};
