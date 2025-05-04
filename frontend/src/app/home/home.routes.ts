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
];
