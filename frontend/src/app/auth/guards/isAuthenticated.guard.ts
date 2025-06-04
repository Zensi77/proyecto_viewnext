import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const isAuthenticatedGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return new Promise((resolve) => {
    const checkUser = () => {
      const isLoading = authService.loading();
      const user = authService.user();

      if (isLoading) {
        setTimeout(checkUser, 100);
      } else if (user) {
        resolve(true);
      } else {
        router.navigateByUrl('/auth/sign-in');
        resolve(false);
      }
    };
    checkUser();
  });
};
