export default [
  {
    path: '',
    loadComponent: () =>
      import('./pages/dashboard/dashboard.component').then(
        (m) => m.DashboardComponent
      ),
    children: [
      {
        path: 'products',
        loadComponent: () =>
          import('./pages/products-page/product-page.component').then(
            (m) => m.ProductPageComponent
          ),
      },
      {
        path: 'categories',
        loadComponent: () =>
          import('./pages/categories-page/categories-page.component').then(
            (m) => m.CategoriesPageComponent
          ),
      },
      {
        path: 'providers',
        loadComponent: () =>
          import('./pages/providers-page/providers-page.component').then(
            (m) => m.ProvidersPageComponent
          ),
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./pages/users-page/users-page.component').then(
            (m) => m.UsersPageComponent
          ),
      },
    ],
  },
];
