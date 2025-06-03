import { NamesResolver } from './resolvers/products-names.resolver';

export default [
  {
    path: '',
    loadComponent: () => import('./pages/home-page/home-page.component'),
    resolve: {
      names: NamesResolver,
    },
  },
  {
    path: 'search',
    loadComponent: () => import('./pages/search-page/search-page.component'),
    resolve: {
      names: NamesResolver,
    },
  },
  {
    path: 'product/:id',
    loadComponent: () => import('./pages/product-page/product-page.component'),
  },
  {
    path: 'checkout',
    loadComponent: () =>
      import('./pages/checkout-page/checkout-page.component'),
  },
  {
    path: 'orders',
    loadComponent: () => import('./pages/orders-page/orders-page.component'),
  },
  {
    path: 'wishlist',
    loadComponent: () =>
      import('./pages/wishList-page/wishList-page.component'),
  },
];
