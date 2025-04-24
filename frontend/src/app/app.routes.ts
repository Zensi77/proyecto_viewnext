import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./home/home.routes'),
  },
  {
    path: '/admin',
    loadChildren: () => import('./admin/admin.routes'),
  },
];
