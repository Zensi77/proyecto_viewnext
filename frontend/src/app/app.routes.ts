import { Routes } from '@angular/router';
import { isNotAuthenticatedGuard } from './auth/guards/isNotAuthenticated.guard';
import { isAdminGuard } from './auth/guards/isAdmin.guard';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./home/home.routes'),
  },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes'),
    canActivate: [isNotAuthenticatedGuard],
  },
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.routes'),
    canActivate: [isAdminGuard],
  },
  {
    path: '**',
    loadComponent: () => import('./shared/pages/not-found/not-found.component'),
  },
];
