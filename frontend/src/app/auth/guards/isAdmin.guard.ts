import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const isAdminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin) {
    router.navigateByUrl('/admin');
    return true;
  } else {
    router.navigateByUrl('/');
    return false;
  }
};
