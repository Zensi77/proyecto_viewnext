import { NamesResolver } from './resolvers/products-names.resolver';

export default [
  {
    path: '',
    loadComponent: () =>
      import('./pages/home-page/home-page.component').then(
        (m) => m.HomePageComponent
      ),
    resolve: {
      names: NamesResolver,
    },
  },
  {
    path: 'search',
    loadComponent: () =>
      import('./pages/search-page/search-page.component').then(
        (m) => m.SearchPageComponent
      ),
    resolve: {
      names: NamesResolver,
    },
  },
  {
    path: 'product/:id',
    loadComponent: () =>
      import('./pages/product-page/product-page.component').then(
        (m) => m.ProductPageComponent
      ),
  },
  {
    path: 'checkout',
    loadComponent: () =>
      import('./pages/checkout-page/checkout-page.component').then(
        (m) => m.CheckoutPageComponent
      ),
  },
  {
    path: 'orders',
    loadComponent: () =>
      import('./pages/orders-page/orders-page.component').then(
        (m) => m.OrdersPageComponent
      ),
  },
];
